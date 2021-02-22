package com.hungerbox.customer.contest.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.text.Html
import android.widget.TextView
import android.text.Spannable
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hungerbox.customer.R
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants

class TermsAndCondition : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var wvTerms: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)
        toolbar = findViewById(R.id.tl_terms)
        tvTitle = findViewById(R.id.tv_toolbar_title)
        wvTerms = this.findViewById(R.id.wv_terms)

        tvTitle.setText("Terms & Conditions")
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val isurl: Boolean = intent.getBooleanExtra("isUrl", false);
        val headerTitle:String? = intent.getStringExtra(ApplicationConstants.HEADER_TITLE);

        if(headerTitle.isNullOrEmpty()){
            tvTitle.setText("Terms & Conditions")
        }else{
            tvTitle.setText(headerTitle)
        }

        wvTerms.setWebViewClient(WebViewClient());
        if (isurl) {
            val url : String  = intent.getStringExtra("url");
            wvTerms.loadUrl(url);

        }
        else {
            var terms: String = intent.getStringExtra(ApplicationConstants.TERMS_AND_CONDITION)
            if (terms == null || terms.trim().isEmpty()) {
                terms = "No Terms And Condition"
            }
            wvTerms.loadData(terms, "text/html; charset=UTF-8", null)
        }

    }
}
