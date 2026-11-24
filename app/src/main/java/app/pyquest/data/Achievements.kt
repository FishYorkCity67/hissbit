package app.pyquest.data

import app.pyquest.i18n.Txt

/** Kennzahlen, aus denen Erfolge berechnet werden. */
data class AchievementStats(
    val totalXp: Int,
    val longestStreak: Int,
    val lessonsDone: Int,
    val unitsDone: Int,
    /** Bester Kurs: abgeschlossene Einheiten × 2 − alle Einheiten (≥ 0 = Hälfte geschafft). */
    val halfMetric: Int,
    /** Bester Kurs: abgeschlossene − alle Einheiten (≥ 0 = Kurs komplett). */
    val courseDoneMetric: Int,
    val perfectLessons: Int,
    val codeRuns: Int,
    val maxCombo: Int,
    val goalDays: Int,
    val challenges: Int,
    val mistakesFixed: Int,
    val purchases: Int,
    val practiceSessions: Int,
    val flags: Set<String>,
)

enum class AchievementIcon { FLAME, BOLT, STAR, TROPHY, CODE, MOON, SUN, TARGET, GEM, BRAIN, MEDAL, ROCKET, BUG, CROWN }

/** Achtung: Die Namen der Einträge sind im Fortschritt gespeichert – nicht umbenennen! */
enum class Achievement(
    val title: Txt,
    val description: Txt,
    val icon: AchievementIcon,
    val gems: Int,
    val target: Int,
    val metric: (AchievementStats) -> Int,
) {
    FIRST_LESSON(Txt("Erste Schritte", "First steps"), Txt("Schließe deine erste Lektion ab.", "Finish your first lesson."), AchievementIcon.STAR, 10, 1, { it.lessonsDone }),
    LESSONS_10(Txt("Fleißige Schlange", "Busy snake"), Txt("Schließe 10 Lektionen ab.", "Finish 10 lessons."), AchievementIcon.MEDAL, 20, 10, { it.lessonsDone }),
    LESSONS_40(Txt("Wissensdurst", "Thirst for knowledge"), Txt("Schließe 40 Lektionen ab.", "Finish 40 lessons."), AchievementIcon.BRAIN, 50, 40, { it.lessonsDone }),
    STREAK_3(Txt("Funke", "Spark"), Txt("Erreiche einen Streak von 3 Tagen.", "Reach a 3-day streak."), AchievementIcon.FLAME, 15, 3, { it.longestStreak }),
    STREAK_7(Txt("Wochenflamme", "Week flame"), Txt("Erreiche einen Streak von 7 Tagen.", "Reach a 7-day streak."), AchievementIcon.FLAME, 30, 7, { it.longestStreak }),
    STREAK_30(Txt("Lauffeuer", "Wildfire"), Txt("Erreiche einen Streak von 30 Tagen.", "Reach a 30-day streak."), AchievementIcon.FLAME, 100, 30, { it.longestStreak }),
    STREAK_100(Txt("Unauslöschlich", "Unstoppable"), Txt("Erreiche einen Streak von 100 Tagen.", "Reach a 100-day streak."), AchievementIcon.FLAME, 300, 100, { it.longestStreak }),
    XP_100(Txt("Aufgewärmt", "Warmed up"), Txt("Sammle 100 XP.", "Earn 100 XP."), AchievementIcon.BOLT, 10, 100, { it.totalXp }),
    XP_1000(Txt("Kraftpaket", "Powerhouse"), Txt("Sammle 1.000 XP.", "Earn 1,000 XP."), AchievementIcon.BOLT, 50, 1000, { it.totalXp }),
    XP_5000(Txt("Code-Profi", "Code pro"), Txt("Sammle 5.000 XP.", "Earn 5,000 XP."), AchievementIcon.BOLT, 150, 5000, { it.totalXp }),
    PERFECT_1(Txt("Fehlerfrei", "Flawless"), Txt("Schließe eine Lektion ohne Fehler ab.", "Finish a lesson without mistakes."), AchievementIcon.TARGET, 10, 1, { it.perfectLessons }),
    PERFECT_10(Txt("Perfektionist", "Perfectionist"), Txt("Schließe 10 Lektionen ohne Fehler ab.", "Finish 10 lessons without mistakes."), AchievementIcon.TARGET, 40, 10, { it.perfectLessons }),
    UNIT_1(Txt("Grundstein", "Foundation"), Txt("Schließe deine erste Einheit ab.", "Finish your first unit."), AchievementIcon.TROPHY, 30, 1, { it.unitsDone }),
    UNIT_HALF(Txt("Halbzeit", "Halfway there"), Txt("Schließe die Hälfte aller Einheiten eines Kurses ab.", "Finish half of all units in a course."), AchievementIcon.TROPHY, 80, 0, { it.halfMetric }),
    COURSE_DONE(Txt("Kurs-Champion", "Course champion"), Txt("Schließe einen kompletten Kurs ab.", "Finish a complete course."), AchievementIcon.CROWN, 300, 0, { it.courseDoneMetric }),
    CODE_10(Txt("Tüftelei", "Tinkerer"), Txt("Führe 10 Programme im Spielplatz aus.", "Run 10 programs in the playground."), AchievementIcon.CODE, 10, 10, { it.codeRuns }),
    CODE_100(Txt("Hacker-Modus", "Hacker mode"), Txt("Führe 100 Programme im Spielplatz aus.", "Run 100 programs in the playground."), AchievementIcon.CODE, 50, 100, { it.codeRuns }),
    COMBO_10(Txt("In Fahrt", "On a roll"), Txt("Beantworte 10 Aufgaben in Folge richtig.", "Answer 10 exercises in a row correctly."), AchievementIcon.ROCKET, 20, 10, { it.maxCombo }),
    GOAL_7(Txt("Zielstrebig", "Goal getter"), Txt("Erreiche dein Tagesziel an 7 Tagen.", "Reach your daily goal on 7 days."), AchievementIcon.TARGET, 30, 7, { it.goalDays }),
    CHALLENGE_1(Txt("Herausforderung angenommen", "Challenge accepted"), Txt("Löse eine Tages-Challenge.", "Solve a daily challenge."), AchievementIcon.ROCKET, 15, 1, { it.challenges }),
    CHALLENGE_10(Txt("Challenge-Champion", "Challenge champion"), Txt("Löse 10 Tages-Challenges.", "Solve 10 daily challenges."), AchievementIcon.CROWN, 60, 10, { it.challenges }),
    FIXED_10(Txt("Aus Fehlern lernen", "Learning from mistakes"), Txt("Korrigiere 10 Fehler in der Wiederholung.", "Fix 10 mistakes in review."), AchievementIcon.BUG, 20, 10, { it.mistakesFixed }),
    PRACTICE_5(Txt("Übung macht den Meister", "Practice makes perfect"), Txt("Absolviere 5 Übungsrunden.", "Complete 5 practice rounds."), AchievementIcon.BRAIN, 20, 5, { it.practiceSessions }),
    SHOPPER(Txt("Shopping-Tour", "Shopping spree"), Txt("Kaufe etwas im Shop.", "Buy something in the shop."), AchievementIcon.GEM, 5, 1, { it.purchases }),
    NIGHT_OWL(Txt("Nachteule", "Night owl"), Txt("Lerne nach 22 Uhr.", "Learn after 10 pm."), AchievementIcon.MOON, 15, 1, { if ("night" in it.flags) 1 else 0 }),
    EARLY_BIRD(Txt("Frühaufsteher", "Early bird"), Txt("Lerne vor 7 Uhr morgens.", "Learn before 7 am."), AchievementIcon.SUN, 15, 1, { if ("early" in it.flags) 1 else 0 });

    /** Ziel für die Fortschrittsanzeige (bei relativen Metriken 1). */
    val displayTarget: Int get() = if (target <= 0) 1 else target

    fun isReached(stats: AchievementStats): Boolean = metric(stats) >= target

    fun progress(stats: AchievementStats): Float =
        if (target <= 0) (if (isReached(stats)) 1f else 0f)
        else (metric(stats).toFloat() / target).coerceIn(0f, 1f)
}

/** Kennzahlen über alle Kurse hinweg (Lektions-IDs sind kursübergreifend eindeutig). */
fun UserProgress.achievementStats(courses: List<Course>) = AchievementStats(
    totalXp = totalXp,
    longestStreak = maxOf(longestStreak, streak),
    lessonsDone = courses.sumOf { it.completedLessonCount(this) },
    unitsDone = courses.sumOf { it.completedUnitCount(this) },
    halfMetric = courses.maxOfOrNull { it.completedUnitCount(this) * 2 - it.units.size } ?: -1,
    courseDoneMetric = courses.maxOfOrNull { it.completedUnitCount(this) - it.units.size } ?: -1,
    perfectLessons = perfectLessons,
    codeRuns = codeRuns,
    maxCombo = maxCombo,
    goalDays = goalDays.size,
    challenges = challengeDays.size + courseChallengeDays.size,
    mistakesFixed = mistakesFixed,
    purchases = purchases,
    practiceSessions = practiceSessions,
    flags = flags.toSet(),
)

/** Schaltet neu erreichte Erfolge frei und schreibt deren Bits gut. */
fun UserProgress.unlockAchievements(courses: List<Course>, now: Long = System.currentTimeMillis()): Pair<UserProgress, List<Achievement>> {
    val stats = achievementStats(courses)
    val fresh = Achievement.entries.filter { it.name !in achievements && it.isReached(stats) }
    if (fresh.isEmpty()) return this to emptyList()
    return copy(
        achievements = achievements + fresh.associate { it.name to now },
        gems = gems + fresh.sumOf { it.gems },
    ) to fresh
}
