package apk.zeffect.cn.favour.base.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import apk.zeffect.cn.favour.R


/**
 * Created by Zeffect on 2018/2/24.
 */
class LoginFrag : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }
}