package apk.zeffect.cn.favour.base.ui

import android.os.Bundle
import apk.zeffect.cn.favour.R
import apk.zeffect.cn.favour.base.base.BaseActivity
import apk.zeffect.cn.favour.base.base.Constant
import apk.zeffect.cn.favour.base.MainAct
import apk.zeffect.cn.favour.base.base.LoginEvt
import apk.zeffect.cn.favour.base.base.MsgModel
import com.zhy.http.okhttp.OkHttpUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import zeffect.cn.common.network.NetUtils
import zeffect.cn.common.sp.SpUtils

/**
 * 插屏广告页
 * Created by Zeffect on 2018/2/24.
 */
class FlashAct : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_flash)
        //闪屏要有时间
        doAsync {
            val login = isLogin()
            //默认登录成功
            uiThread {
                if (login) startActivity<MainAct>()
                else LoginFrag().show(supportFragmentManager, LoginFrag::class.java.name)
            }
        }
    }


    /***
     * 检查是否登录
     *
     */
    private fun isLogin(): Boolean {
        return true
        //有无验证码
        val authCode = SpUtils.getString(mContext, Constant.Sp.AUTH_CODE)
        if (authCode?.isNullOrBlank() != false) return false
        //有无电话号码
        val userPhone = SpUtils.getString(mContext, Constant.Sp.USER_PHONE)
        if (userPhone?.isNullOrBlank() != false) return false
        //有无网络
        if (!NetUtils.isConnected(mContext)) return false
        //验证码是否正确
        val response = OkHttpUtils.post().addParams("appkey", "2444b9a287a64")
                .addParams("phone", userPhone)
                .addParams("zone", "86")
                .addParams("code", authCode).build().execute()
        if (!response.isSuccessful) return false
        val responseStr = response.body().string()
        val dataJson = JSONObject(responseStr)
        if (dataJson.isNull(Constant.Key.STATUS)) return false
        val statusCode = dataJson.getInt(Constant.Key.STATUS)
        return statusCode == 200
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private fun loginResult(msgModel: MsgModel) {
        if (msgModel.model == MsgModel.models.loginEvt) {

        }
    }

}