package apk.zeffect.cn.favour.base.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import apk.zeffect.cn.favour.R
import apk.zeffect.cn.favour.base.base.BaseDialogFragment
import apk.zeffect.cn.favour.base.base.Constant
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import org.jetbrains.anko.find
import zeffect.cn.common.sp.SpUtils
import zeffect.cn.common.weak.WeakHandler
import java.util.*
import kotlin.concurrent.timerTask


/**
 * Created by Zeffect on 2018/2/24.
 */
class LoginFrag : BaseDialogFragment() {
    private val PHONE_NUMBER = 11
    private val CODE_NUM = 4
    private lateinit var mView: View
    private val MAX_TIME = 60
    private val DEFAULT_COUNTRY = "86"
    private var time = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mView = inflater.inflate(R.layout.dialog_login, container, false)
        initView(mView)
        return mView
    }


    private val codeBtn by lazy { mView.find<Button>(R.id.getCode) }
    private val loginBtn by lazy { mView.find<Button>(R.id.login) }
    private val phoneTIL by lazy { mView.find<TextInputLayout>(R.id.phone_til) }
    private val phoneInput by lazy { mView.find<TextInputEditText>(R.id.phone_input) }
    private val codeTil by lazy { mView.find<TextInputLayout>(R.id.code_til) }
    private val codeInput by lazy { mView.find<TextInputEditText>(R.id.code_input) }

    private val TIME_WHAT = 0X11
    private val TIME_END_WHAT = 0X12
    private val SEND_CODE_RESULT = 0X13
    private val mHandler by lazy {
        WeakHandler(mContext?.mainLooper, Handler.Callback { message: Message? ->
            when (message?.what) {
                TIME_WHAT -> codeBtn.text = "${mContext?.resources?.getString(R.string.get_auth_code)}(${MAX_TIME - time})"
                TIME_END_WHAT -> {
                    codeBtn.text = "${mContext?.resources?.getString(R.string.get_auth_code)}"
                    codeBtn.isEnabled = true
                    phoneInput.isEnabled = true
                }
                SEND_CODE_RESULT -> {
                    codeInput.isEnabled = true
                    codeBtn.isEnabled = false
                    phoneInput.isEnabled = false
                }
            };true
        })
    }

    private fun initView(view: View) {
        //默认两个按钮不可操作
        codeBtn.isEnabled = false
        loginBtn.isEnabled = false
        codeInput.isEnabled = false
        //手机号输入监听
        phoneInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                codeBtn.isEnabled = p0?.length == PHONE_NUMBER
            }
        })
        //验证码输入监听
        codeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                loginBtn.isEnabled = p0?.length ?: 0 >= CODE_NUM
            }
        })
        //获取验证码按钮监听
        codeBtn.setOnClickListener {
            SMSSDK.registerEventHandler(object : EventHandler() {
                override fun afterEvent(p0: Int, p1: Int, p2: Any?) {
                    if (p1 == SMSSDK.RESULT_COMPLETE) {
                        mHandler.sendEmptyMessage(SEND_CODE_RESULT)
                        //发送短信成功,开始倒计时
                        time = 0
                        Timer().schedule(timerTask {
                            time++
                            if (time >= MAX_TIME) {
                                cancel()
                                mHandler.sendEmptyMessage(TIME_END_WHAT)
                            } else mHandler.sendEmptyMessage(TIME_WHAT)
                        }, 0, 1000)
                    } else {
                        Snackbar.make(mView, R.string.get_auth_code_error, Snackbar.LENGTH_SHORT).show()
                    }
                }
            })
            // 触发操作
            SMSSDK.getVerificationCode(DEFAULT_COUNTRY, phoneInput.text.toString())
        }
        //登录按钮
        loginBtn.setOnClickListener {
            loginBtn.isEnabled = false
            codeInput.isEnabled = false
            phoneInput.isEnabled = false
            codeBtn.isEnabled = false
            val userPhone = phoneInput.text.toString()
            val authCode = codeInput.text.toString()

            SMSSDK.registerEventHandler(object : EventHandler() {
                override fun afterEvent(p0: Int, p1: Int, p2: Any?) {
                    super.afterEvent(p0, p1, p2)
                    if (p1 == SMSSDK.RESULT_COMPLETE) {
                        SpUtils.putString(mContext!!, Constant.Sp.USER_PHONE, userPhone)
                        SpUtils.putString(mContext!!, Constant.Sp.AUTH_CODE, authCode)
                    } else {
                        Snackbar.make(mView, R.string.phone_or_code_wrong, Snackbar.LENGTH_SHORT).show()
                    }
                }
            })
            // 触发操作
            SMSSDK.submitVerificationCode(DEFAULT_COUNTRY, userPhone, authCode)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SMSSDK.unregisterAllEventHandler();
    }
}