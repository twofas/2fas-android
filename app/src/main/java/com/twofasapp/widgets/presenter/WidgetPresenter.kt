package com.twofasapp.widgets.presenter

class WidgetPresenter(
    private val widgetPresenterDelegate: WidgetPresenterDelegate,
    private val widgetItemPresenterDelegate: WidgetItemPresenterDelegate,
    private val widgetReceiverPresenterDelegate: WidgetReceiverPresenterDelegate,
) :
    WidgetPresenterDelegate by widgetPresenterDelegate,
    WidgetItemPresenterDelegate by widgetItemPresenterDelegate,
    WidgetReceiverPresenterDelegate by widgetReceiverPresenterDelegate {
}