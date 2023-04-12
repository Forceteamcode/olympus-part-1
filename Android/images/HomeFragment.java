package cc.olps.home.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private View view;
    private View loginAreaV;
    private View myRateAreaV;
    private TextView myRateTV;
    private TextView myPercentTV;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.e("homefragment onCreate", "====" + this);
        loginAreaV = view.findViewById(R.id.login_tip_area);
        myRateAreaV = view.findViewById(R.id.my_rate_area);
        myRateTV = view.findViewById(R.id.my_hashrate);
        myPercentTV = view.findViewById(R.id.my_rate_percent);
        TextView tv = loginAreaV.findViewById(R.id.to_signup);
        tv.setOnClickListener(onClickListener);
        TextViewUtil.setTextColorVertical(tv,R.color.colorBlueStart,R.color.colorBlue);

        WebView webView = view.findViewById(R.id.all_hashrate);
        webView.setBackgroundColor(getResources().getColor(R.color.colorMainBG));
        WebSettings set = webView.getSettings();
        set.setAllowFileAccess(true);
        set.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/line/line.html");
        webView.setVisibility(View.INVISIBLE);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserUtil.isLogin()) {
            loginAreaV.setVisibility(View.GONE);
            myRateAreaV.setVisibility(View.VISIBLE);
            getData();
        } else {
            loginAreaV.setVisibility(View.VISIBLE);
            myRateAreaV.setVisibility(View.GONE);

        }
        Log.e("homefragment onResume", "====");
    }

    private void getData() {

        HttpEncryptRequestParams params = new HttpEncryptRequestParams();
        HttpRequest.post(Constant.APP_INDEX, params, new HttpCallBack() {
            @Override
            public void onFailure() {
                ToastUtil.shortShow(getString(R.string.request_timeout));
            }

            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        myRateTV.setText(jsonObject.getJSONObject("data").getString("userPower"));
                        myPercentTV.setText(jsonObject.getJSONObject("data").getString("userPowerPercent"));
                    } else {
                        if (UserUtil.isEn()) {
                            AlertMsgDialog.showDialog(getActivity(), jsonObject.getString("msgEn"), null);
                        } else {
                            AlertMsgDialog.showDialog(getActivity(), jsonObject.getString("msg"), null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.to_signup: {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    ((MainActivity) getActivity()).intentFromBottom(intent);
                    break;
                }
            }
        }
    };
    }

