package app.pyquest.data

import app.pyquest.i18n.Txt
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max
import kotlin.math.roundToInt

enum class SessionMode { LESSON, CHECKPOINT, PRACTICE, MISTAKES, CODE_PRACTICE, CHALLENGE }

/** Übungsrunden: bringen Herzen und zählen für den Erfolg „Übung macht den Meister“ (Challenges nicht). */
val PracticeModes = setOf(SessionMode.PRACTICE, SessionMode.MISTAKES, SessionMode.CODE_PRACTICE)

/** Ergebnis einer abgeschlossenen Sitzung, wie es der Lektions-Bildschirm meldet. */
data class SessionResult(
    val mode: SessionMode,
    val lessonId: String?,
    val total: Int,
    val mistakes: Int,
    val durationMs: Long,
    val maxCombo: Int,
    val codeSolved: Int,
    val fixedMistakes: List<String>,
    val newMistakes: List<String>,
) {
    val accuracy: Int
        get() = if (total + mistakes == 0) 100 else ((total.toFloat() / (total + mistakes)) * 100).roundToInt()
    val perfect: Boolean get() = mistakes == 0
}

/** Was die Sitzung eingebracht hat – für die Ergebnis-Screens. */
data class Reward(
    val result: SessionResult,
    val xp: Int,
    val gems: Int,
    val boosted: Boolean,
    val streakExtended: Boolean,
    val streak: Int,
    val goalReachedNow: Boolean,
    val xpToday: Int,
    val dailyGoal: Int,
    val achievements: List<Achievement>,
    val unitCompleted: CourseUnit?,
    val heartsGained: Int,
)

object Game {
    const val MAX_HEARTS = 5
    const val HEART_REFILL_MS = 20L * 60 * 1000
    const val MAX_FREEZES = 2
    const val PRICE_FREEZE = 60
    const val PRICE_REFILL = 40
    const val PRICE_BOOST = 50
    const val PRICE_REPAIR = 120
    const val BOOST_MS = 15L * 60 * 1000

    fun dayKey(date: LocalDate): String = date.toString()
}

// ---------------------------------------------------------------- Abfragen

fun UserProgress.isCompleted(lessonId: String) = (lessons[lessonId]?.completions ?: 0) > 0

fun UserProgress.xpOn(date: LocalDate) = xpByDay[Game.dayKey(date)] ?: 0

fun UserProgress.streakDoneToday(today: LocalDate = LocalDate.now()) = lastStreakDay == Game.dayKey(today)

fun UserProgress.boostActive(now: Long = System.currentTimeMillis()) = boostUntil > now

fun UserProgress.challengeDoneToday(today: LocalDate = LocalDate.now()) = Game.dayKey(today) in challengeDays

/** Challenge des Tages für einen bestimmten Kurs schon gelöst? */
fun UserProgress.challengeDoneToday(course: CourseId, today: LocalDate = LocalDate.now()) =
    if (course == CourseId.PYTHON) challengeDoneToday(today) else "${course.key}:${Game.dayKey(today)}" in courseChallengeDays

/** Streak, wie er angezeigt wird (0, falls er gerissen ist und noch nicht verrechnet wurde). */
fun UserProgress.currentStreak(today: LocalDate = LocalDate.now()): Int {
    val last = lastStreakDay?.let(LocalDate::parse) ?: return 0
    val gap = ChronoUnit.DAYS.between(last, today)
    return when {
        gap <= 1 -> streak
        gap - 1 <= streakFreezes -> streak
        else -> 0
    }
}

fun UserProgress.isActiveDay(date: LocalDate) = (xpByDay[Game.dayKey(date)] ?: 0) > 0

fun UserProgress.isFrozenDay(date: LocalDate) = Game.dayKey(date) in frozenDays

// ---------------------------------------------------------------- Freischalten

/** Startpunkt im Kurs (übersprungene Einheiten sind frei). Python nutzt das alte Feld [UserProgress.startUnit]. */
fun UserProgress.startUnitFor(course: Course): Int =
    if (course.id == CourseId.PYTHON) startUnit else startUnits[course.id.key] ?: 1

fun Course.isUnlocked(lesson: Lesson, p: UserProgress): Boolean {
    val idx = indexOf(lesson.id)
    if (idx <= 0) return true
    val unit = unitOf(lesson)
    val start = p.startUnitFor(this)
    if (unit.number < start) return true
    if (unit.number == start && unit.lessons.first().id == lesson.id) return true
    return p.isCompleted(lessons[idx - 1].id)
}

/** Die Lektion, die als nächstes dran ist (mit "Start"-Markierung). */
fun Course.currentLesson(p: UserProgress): Lesson? {
    val start = p.startUnitFor(this)
    return lessons.firstOrNull { unitOf(it).number >= start && !p.isCompleted(it.id) && isUnlocked(it, p) }
        ?: lessons.firstOrNull { !p.isCompleted(it.id) && isUnlocked(it, p) }
}

fun Course.isUnitCompleted(unit: CourseUnit, p: UserProgress) = unit.lessons.all { p.isCompleted(it.id) }

fun Course.completedLessonCount(p: UserProgress) = lessons.count { p.isCompleted(it.id) }

fun Course.completedUnitCount(p: UserProgress) = units.count { isUnitCompleted(it, p) }

// ---------------------------------------------------------------- Zustandsänderungen

/** Herzen nachfüllen und verpasste Tage (Streak-Freeze / gerissener Streak) verrechnen. */
fun UserProgress.settled(now: Long = System.currentTimeMillis(), today: LocalDate = LocalDate.now()): UserProgress =
    refillHearts(now).settleStreak(today)

fun UserProgress.refillHearts(now: Long): UserProgress {
    if (hearts >= Game.MAX_HEARTS) return if (heartsRefillAt != 0L) copy(heartsRefillAt = 0) else this
    if (heartsRefillAt == 0L) return copy(heartsRefillAt = now + Game.HEART_REFILL_MS)
    var h = hearts
    var t = heartsRefillAt
    while (h < Game.MAX_HEARTS && now >= t) {
        h++
        t += Game.HEART_REFILL_MS
    }
    return copy(hearts = h, heartsRefillAt = if (h >= Game.MAX_HEARTS) 0 else t)
}

fun UserProgress.loseHeart(now: Long): UserProgress {
    val h = max(0, hearts - 1)
    return copy(hearts = h, heartsRefillAt = if (heartsRefillAt == 0L) now + Game.HEART_REFILL_MS else heartsRefillAt)
}

fun UserProgress.settleStreak(today: LocalDate): UserProgress {
    val last = lastStreakDay?.let(LocalDate::parse) ?: return this
    val gap = ChronoUnit.DAYS.between(last, today)
    if (gap <= 1 || streak == 0) return this
    val missed = (gap - 1).toInt()
    return if (missed <= streakFreezes) {
        val frozen = (1..missed).map { Game.dayKey(last.plusDays(it.toLong())) }
        copy(
            streakFreezes = streakFreezes - missed,
            frozenDays = (frozenDays + frozen).distinct().takeLast(500),
            lastStreakDay = Game.dayKey(today.minusDays(1)),
        )
    } else {
        copy(lostStreak = streak, streak = 0)
    }
}

/** Gerissenen Streak gegen Edelsteine reparieren. */
fun UserProgress.repairStreak(today: LocalDate): UserProgress? {
    if (lostStreak <= 0 || gems < Game.PRICE_REPAIR) return null
    return copy(
        gems = gems - Game.PRICE_REPAIR,
        streak = lostStreak,
        lostStreak = 0,
        lastStreakDay = Game.dayKey(today.minusDays(1)),
        purchases = purchases + 1,
    )
}

private fun UserProgress.extendStreak(today: LocalDate): Pair<UserProgress, Boolean> {
    val todayKey = Game.dayKey(today)
    if (lastStreakDay == todayKey) return this to false
    val last = lastStreakDay?.let(LocalDate::parse)
    val newStreak = if (last != null && last == today.minusDays(1) && streak > 0) streak + 1 else 1
    return copy(
        streak = newStreak,
        lastStreakDay = todayKey,
        longestStreak = max(longestStreak, newStreak),
        lostStreak = 0,
    ) to true
}

/** Verbucht eine abgeschlossene Sitzung: XP, Edelsteine, Streak, Fehlerliste und Erfolge. */
fun UserProgress.applySession(
    r: SessionResult,
    course: Course,
    allCourses: List<Course>,
    settings: AppSettings,
    now: Long = System.currentTimeMillis(),
    today: LocalDate = LocalDate.now(),
    hour: Int = java.time.LocalTime.now().hour,
): Pair<UserProgress, Reward> {
    var p = settled(now, today)
    val boosted = p.boostActive(now)

    val baseXp = when (r.mode) {
        SessionMode.LESSON -> 10
        SessionMode.CHECKPOINT -> 20
        SessionMode.PRACTICE, SessionMode.MISTAKES -> 8
        SessionMode.CODE_PRACTICE -> 12
        SessionMode.CHALLENGE -> 20
    }
    var xp = baseXp + (if (r.perfect) 5 else 0) + r.codeSolved * 2 + (r.maxCombo / 5)
    if (boosted) xp *= 2

    var gems = when (r.mode) {
        SessionMode.LESSON -> 5
        SessionMode.CHECKPOINT -> 25
        SessionMode.CHALLENGE -> 20
        else -> 3
    } + (if (r.perfect) 3 else 0)

    val todayKey = Game.dayKey(today)
    val xpBefore = p.xpByDay[todayKey] ?: 0
    val xpAfter = xpBefore + xp
    val goalReachedNow = xpBefore < settings.dailyGoal && xpAfter >= settings.dailyGoal
    if (goalReachedNow) gems += 10

    // Lektionsfortschritt
    var lessons = p.lessons
    var unitCompleted: CourseUnit? = null
    if (r.lessonId != null && (r.mode == SessionMode.LESSON || r.mode == SessionMode.CHECKPOINT)) {
        val old = lessons[r.lessonId] ?: LessonRecord()
        val wasDone = old.completions > 0
        lessons = lessons + (r.lessonId to old.copy(
            completions = old.completions + 1,
            bestAccuracy = max(old.bestAccuracy, r.accuracy),
            lastCompleted = now,
        ))
        val lesson = course.lesson(r.lessonId)
        if (!wasDone && lesson != null) {
            val unit = course.unitOf(lesson)
            if (unit.lessons.all { it.id == r.lessonId || p.isCompleted(it.id) }) unitCompleted = unit
        }
    }

    // Fehlerliste pflegen
    val mistakes = (p.mistakes.filterNot { it in r.fixedMistakes } + r.newMistakes).distinct().takeLast(80)

    // Herzen fürs Üben
    var heartsGained = 0
    if (r.mode in PracticeModes) {
        heartsGained = minOf(Game.MAX_HEARTS - p.hearts, 1)
    }

    val flags = p.flags.toMutableSet()
    if (hour >= 22 || hour < 4) flags += "night"
    if (hour in 4..6) flags += "early"

    p = p.copy(
        lessons = lessons,
        xpByDay = p.xpByDay + (todayKey to xpAfter),
        totalXp = p.totalXp + xp,
        gems = p.gems + gems,
        hearts = p.hearts + heartsGained,
        mistakes = mistakes,
        mistakesFixed = p.mistakesFixed + r.fixedMistakes.size,
        perfectLessons = p.perfectLessons + if (r.perfect && r.mode == SessionMode.LESSON) 1 else 0,
        maxCombo = max(p.maxCombo, r.maxCombo),
        goalDays = if (xpAfter >= settings.dailyGoal) (p.goalDays + todayKey).distinct().takeLast(1000) else p.goalDays,
        challengeDays = if (r.mode == SessionMode.CHALLENGE && course.id == CourseId.PYTHON) (p.challengeDays + todayKey).distinct().takeLast(1000) else p.challengeDays,
        courseChallengeDays = if (r.mode == SessionMode.CHALLENGE && course.id != CourseId.PYTHON) {
            (p.courseChallengeDays + "${course.id.key}:$todayKey").distinct().takeLast(1000)
        } else p.courseChallengeDays,
        practiceSessions = p.practiceSessions + if (r.mode in PracticeModes) 1 else 0,
        flags = flags.toList(),
    ).refillHearts(now)

    val (withStreak, extended) = p.extendStreak(today)
    p = withStreak

    val (withAchievements, unlocked) = p.unlockAchievements(allCourses, now)
    p = withAchievements

    return p to Reward(
        result = r,
        xp = xp,
        gems = gems + unlocked.sumOf { it.gems },
        boosted = boosted,
        streakExtended = extended,
        streak = p.streak,
        goalReachedNow = goalReachedNow,
        xpToday = xpAfter,
        dailyGoal = settings.dailyGoal,
        achievements = unlocked,
        unitCompleted = unitCompleted,
        heartsGained = heartsGained,
    )
}

// ---------------------------------------------------------------- Shop

enum class ShopItem(val id: String, val title: Txt, val description: Txt, val price: Int, val cosmetic: Boolean) {
    FREEZE("freeze", Txt("Streak-Freeze", "Streak freeze"), Txt("Schützt deinen Streak einen Tag lang, wenn du mal nicht lernst. Max. 2.", "Protects your streak for a day when you skip learning. Max. 2."), Game.PRICE_FREEZE, false),
    REFILL("refill", Txt("Herzen auffüllen", "Refill hearts"), Txt("Füllt sofort alle Herzen wieder auf.", "Instantly refills all your hearts."), Game.PRICE_REFILL, false),
    BOOST("boost", Txt("XP-Boost", "XP boost"), Txt("Doppelte XP für 15 Minuten.", "Double XP for 15 minutes."), Game.PRICE_BOOST, false),
    PARTY("hat_party", Txt("Partyhut", "Party hat"), Txt("Für Monty, wenn es was zu feiern gibt.", "For Monty when there's something to celebrate."), 80, true),
    TOPHAT("hat_top", Txt("Zylinder", "Top hat"), Txt("Elegant wie ein echter Gentleman-Programmierer.", "As elegant as a true gentleman coder."), 120, true),
    CAP("hat_cap", Txt("Basecap", "Baseball cap"), Txt("Lässig und sportlich.", "Casual and sporty."), 80, true),
    GRAD("hat_grad", Txt("Doktorhut", "Graduation cap"), Txt("Für alle, die den Kurs durchziehen.", "For everyone who sees the course through."), 150, true),
    WIZARD("hat_wizard", Txt("Zauberhut", "Wizard hat"), Txt("Programmieren ist eigentlich Magie.", "Coding is basically magic."), 160, true),
    CROWN("hat_crown", Txt("Krone", "Crown"), Txt("Für wahre Code-Königinnen und -Könige.", "For true coding royalty."), 250, true),
    GLASSES("glasses", Txt("Nerd-Brille", "Nerd glasses"), Txt("Klug sieht er aus, der Monty.", "Doesn't Monty look clever?"), 100, true),
    SHADES("shades", Txt("Sonnenbrille", "Sunglasses"), Txt("Cool bleiben, auch bei SyntaxErrors.", "Stay cool, even with SyntaxErrors."), 120, true),
    HEADPHONES("headphones", Txt("Kopfhörer", "Headphones"), Txt("Lo-Fi-Beats zum Coden.", "Lo-fi beats for coding."), 140, true),
    BOWTIE("bowtie", Txt("Fliege", "Bow tie"), Txt("Schick für jeden Anlass.", "Smart for any occasion."), 90, true);

    companion object {
        fun byId(id: String?) = entries.firstOrNull { it.id == id }
    }
}

sealed interface PurchaseResult {
    data class Ok(val progress: UserProgress) : PurchaseResult
    data class Failed(val reason: Txt) : PurchaseResult
}

fun UserProgress.purchase(item: ShopItem, now: Long = System.currentTimeMillis()): PurchaseResult {
    if (item.cosmetic && item.id in ownedItems) return PurchaseResult.Failed(Txt("Hast du schon.", "You already own it."))
    if (gems < item.price) return PurchaseResult.Failed(Txt("Nicht genug Bits.", "Not enough bits."))
    val base = copy(gems = gems - item.price, purchases = purchases + 1)
    val next = when (item) {
        ShopItem.FREEZE -> {
            if (streakFreezes >= Game.MAX_FREEZES) {
                return PurchaseResult.Failed(Txt("Du hast schon ${Game.MAX_FREEZES} Streak-Freezes.", "You already have ${Game.MAX_FREEZES} streak freezes."))
            }
            base.copy(streakFreezes = streakFreezes + 1)
        }
        ShopItem.REFILL -> {
            if (hearts >= Game.MAX_HEARTS) return PurchaseResult.Failed(Txt("Deine Herzen sind schon voll.", "Your hearts are already full."))
            base.copy(hearts = Game.MAX_HEARTS, heartsRefillAt = 0)
        }
        ShopItem.BOOST -> base.copy(boostUntil = max(boostUntil, now) + Game.BOOST_MS)
        else -> base.copy(ownedItems = ownedItems + item.id, equipped = item.id)
    }
    return PurchaseResult.Ok(next)
}