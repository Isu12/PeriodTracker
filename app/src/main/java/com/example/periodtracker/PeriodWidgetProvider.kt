package com.example.periodtracker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast

class PeriodWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d("PeriodWidgetProvider", "Received broadcast for widget update")
    }
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget)

        // Get the next period dates from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("PeriodTrackerPrefs", Context.MODE_PRIVATE)
        val nextPeriodStart = sharedPreferences.getString("nextPeriodStart", "Not Set") ?: "Not Set"
        val nextPeriodEnd = sharedPreferences.getString("nextPeriodEnd", "Not Set") ?: "Not Set"

        views.setTextViewText(R.id.nextPeriodText, "$nextPeriodStart")
        views.setTextViewText(R.id.periodEndText, "$nextPeriodEnd")

        // Optionally, add an onClick action to open your app
        val intent = Intent(context, MainActivity3::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.nextPeriodText, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
