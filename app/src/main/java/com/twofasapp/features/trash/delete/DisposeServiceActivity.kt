package com.twofasapp.features.trash.delete

import android.os.Bundle
import android.widget.TextView
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.extensions.clicksThrottled
import com.twofasapp.databinding.ActivityDisposeServiceBinding
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.ui.ServiceActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class DisposeServiceActivity : BaseActivityPresenter<ActivityDisposeServiceBinding>(), DisposeServiceContract.View {

    companion object {
        const val ARG_SERVICE = "service"
    }

    private val presenter: DisposeServiceContract.Presenter by injectThis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityDisposeServiceBinding::inflate)
        setPresenter(presenter)
    }

    override fun deleteClicks() = viewBinding.delete.clicksThrottled()

    override fun cancelClicks() = viewBinding.cancel.clicksThrottled()

    override fun closeClicks() = viewBinding.close.clicksThrottled()

    override fun deleteSwitchChanges(): Flowable<Boolean> =
        viewBinding.deleteSwitch.checkedChanges().toFlowable(BackpressureStrategy.LATEST)

    override fun setDeleteEnabled(isEnabled: Boolean) {
        viewBinding.delete.isEnabled = isEnabled
    }

    override fun getServiceExtra() =
        intent.extras!!.getParcelable<ServiceDto>(ServiceActivity.ARG_SERVICE)!!

    override fun setHeader(serviceName: String?) {
        viewBinding.header.text = serviceName
    }

    override fun setNote(resId: Int?, serviceName: String?) =
        formatText(viewBinding.note, resId, serviceName)

    private fun formatText(textView: TextView, resId: Int?, serviceName: String?) {
        if (resId != null) {
            textView.text = getString(resId, serviceName, serviceName)
        } else {
            textView.text = serviceName
        }
    }
}