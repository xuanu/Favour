package apk.zeffect.cn.favour.base.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import me.imid.swipebacklayout.lib.app.SwipeBackActivity
import org.greenrobot.eventbus.EventBus

/**
 * Created by Zeffect on 2018/2/24.
 */
open class BaseActivity : AppCompatActivity() {
    val mContext by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(mContext)) EventBus.getDefault().register(mContext)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(mContext)) EventBus.getDefault().unregister(mContext)
    }
}


open class BaseFragment : Fragment() {
    val mContext by lazy { context }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (!EventBus.getDefault().isRegistered(mContext)) EventBus.getDefault().register(mContext)
    }

    override fun onDetach() {
        super.onDetach()
        if (EventBus.getDefault().isRegistered(mContext)) EventBus.getDefault().unregister(mContext)
    }
}

open class BaseDialogFragment : DialogFragment() {
    val mContext by lazy { context }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (!EventBus.getDefault().isRegistered(mContext)) EventBus.getDefault().register(mContext)
    }

    override fun onDetach() {
        super.onDetach()
        if (EventBus.getDefault().isRegistered(mContext)) EventBus.getDefault().unregister(mContext)
    }
}

interface Constant {
    object Key {
        val STATUS = "status"
    }

    object Sp {
        val AUTH_CODE = "SP_AUTH_CODE"
        val USER_PHONE = "SP_USER_PHONE"
    }
}


data class MsgModel(val model: MsgModel.models = models.None, val data: Any? = null) {
    enum class models {
        None, loginEvt
    }
}

data class LoginEvt(val status: Boolean)
