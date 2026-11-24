# Python ruft Methoden dieser Klasse per Reflection auf (Chaquopy-Bridge).
-keep class app.pyquest.python.PythonBridge { *; }

# JavaScript ruft die Brücke HB über ihre Methodennamen auf (siehe assets/js/host.js).
-keepclassmembers class app.pyquest.js.JsRunner$Bridge {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface

# Widget-Receiver, Worker und Widget-Aktionen werden über den Klassennamen gestartet.
-keep class app.pyquest.widget.** extends androidx.glance.appwidget.GlanceAppWidgetReceiver { *; }
-keep class * implements androidx.glance.appwidget.action.ActionCallback { *; }
-keep class app.pyquest.** extends androidx.work.ListenableWorker { *; }

-keepattributes *Annotation*, InnerClasses, Signature
