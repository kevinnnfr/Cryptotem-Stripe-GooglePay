package com.zxy.cryptotemu.pay;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.stripe.android.PaymentConfiguration;
import com.stripe.android.googlepaylauncher.GooglePayEnvironment;
import com.stripe.android.googlepaylauncher.GooglePayLauncher;
import com.zxy.cryptotemu.R;
import com.zxy.cryptotemu.base.BaseApplication;
import com.zxy.cryptotemu.utils.LogUtils;
import com.zxy.cryptotemu.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

public class GooglePayActivity extends ComponentActivity {
    private String key="";
    private String scripeId="";
//   
    GooglePayLauncher googlePayLauncher;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f);
        initView();
    }

    public void initView() {
        try{
            //clientSecret
            key = getIntent().getStringExtra("key");
            //transactionId
            scripeId = getIntent().getStringExtra("scripeId");
            if(TextUtils.isEmpty(key)){
                ToastUtils.show(getString(R.string.pay_info_error));
                setResult(RESULT_FIRST_USER);
                finish();
                return;
            }
            LogUtils.log("pay_key = "+BaseApplication.STRIPE_KEY);
            PaymentConfiguration.init(getApplicationContext(),BaseApplication.STRIPE_KEY);

            if(BaseApplication.STRIPE_KEY.startsWith("pk_test")){
                googlePayLauncher = new GooglePayLauncher(
                        this,
                        new GooglePayLauncher.Config(
                            GooglePayEnvironment.Test,
                                "FR",
                                "Cryptotem"
                        ),
                        this::onGooglePayReady,
                        this::onGooglePayResult
                );
            }else{
                googlePayLauncher = new GooglePayLauncher(
                        this,
                        new GooglePayLauncher.Config(
//                                GooglePayEnvironment.Test,
                                GooglePayEnvironment.Production,
                                "FR",
                                "Cryptotem"
                        ),
                        this::onGooglePayReady,
                        this::onGooglePayResult
                );
            }
        }catch (Exception e){
            ToastUtils.show(e.getMessage());
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }

    private void onGooglePayReady(boolean isReady) {
        LogUtils.log("onGooglePayReady = "+isReady);
        if(isReady){
            googlePayLauncher.presentForPaymentIntent(key);
        }else{
            ToastUtils.show(getString(R.string.gpay_not_available));
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }

    private void onGooglePayResult(@NotNull GooglePayLauncher.Result result) {
        if (result instanceof GooglePayLauncher.Result.Completed) {
            // Payment succeeded, show a receipt view
            LogUtils.log("Payment succeeded, show a receipt view");
            setResult(RESULT_OK);
            finish();
        } else if (result instanceof GooglePayLauncher.Result.Canceled) {
            // User canceled the operation
            LogUtils.log("User canceled the operation");
            setResult(RESULT_CANCELED);
            finish();
        } else if (result instanceof GooglePayLauncher.Result.Failed) {
            // Operation failed; inspect `result.getError()` for more details
            LogUtils.log("Operation failed; inspect `result.getError()` for more details");
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }
}
