package com.hungerbox.customer.order.adapter

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hungerbox.customer.HBMixpanel
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.R
import com.hungerbox.customer.model.Product
import com.hungerbox.customer.model.Vendor
import com.hungerbox.customer.order.fragment.GeneralDialogFragment
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.CleverTapEvent.PropertiesNames.Companion.is_bookmarked
import com.hungerbox.customer.util.CleverTapEvent.PropertiesNames.Companion.is_trending
import com.hungerbox.customer.util.CleverTapEvent.PropertiesNames.Companion.menu_item_id
import com.hungerbox.customer.util.CleverTapEvent.PropertiesNames.Companion.source
import com.hungerbox.customer.util.CleverTapEvent.PropertiesNames.Companion.userId
import com.hungerbox.customer.util.CleverTapEvent.PropertiesNames.Companion.vendor_id
import com.hungerbox.customer.util.EventUtil
import com.hungerbox.customer.util.SharedPrefUtil
import com.hungerbox.customer.util.view.HbTextView
import org.json.JSONObject
import java.util.*

class HorizontalBookmarkAdapter(var activity:AppCompatActivity, var productList:ArrayList<Object>,var vendors:ArrayList<Vendor>,var occasionId:Long, var typeBookmark:Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_TYPE_LABEL = 22
    private val ITEM_TYPE_PRODUCT = 24
    private val  context: Context = activity as Context
    private val mainApplication: MainApplication = activity.application as MainApplication

    class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName = itemView.findViewById<HbTextView>(R.id.tv_item_name)
        val tvPrice = itemView.findViewById<HbTextView>(R.id.tv_item_price)
        val ivItemAdd = itemView.findViewById<AppCompatImageView>(R.id.iv_item_add)
        val ivIsVeg = itemView.findViewById<AppCompatImageView>(R.id.iv_veg_non)
        val clItemBody : ConstraintLayout = itemView.findViewById(R.id.cl_item_body)
    }

    class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLabel = itemView.findViewById<HbTextView>(R.id.tv_trending)
        val ivLabel = itemView.findViewById<AppCompatImageView>(R.id.iv_trending)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == ITEM_TYPE_LABEL){
            LabelViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mini_trending_label_image, parent, false))
        } else{
            BookmarkViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mini_trending_item, parent, false))
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LabelViewHolder) bindLabelViewHolder(holder,position) else bindBookmarkViewHolder(holder as BookmarkViewHolder,position)
    }

    private fun bindLabelViewHolder(holder : LabelViewHolder, position: Int){
        if (typeBookmark){
            holder.tvLabel.text = context.getString(R.string.favourite_item)
            holder.ivLabel.setImageResource(R.drawable.ic_bookmark_circular)
        } else{
            holder.tvLabel.text = context.getString(R.string.trending_item)
            holder.ivLabel.setImageResource(R.drawable.ic_trending_circular)
        }
    }

    private fun bindBookmarkViewHolder(holder : BookmarkViewHolder, position: Int){
        if (productList[position] is Product) {
            val product: Product = productList[position] as Product
            holder.tvItemName.text = product.name
            holder.tvItemName.isSelected = true

            if (product.isFree()) {
                if (product.discountedPrice == 0.0) {
                    holder.tvPrice.text = if (AppUtils.getConfig(context).isHide_price) "" else AppUtils.getConfig(context).company_paid_text
                    if (product.isFree() && AppUtils.getConfig(context).is_guest_order) {
                        holder.tvPrice.text = context.getString(R.string.guest_ordering_text_small)
                    }
                } else if (AppUtils.getConfig(context).isHide_discount) {
                    holder.tvPrice.text = ("₹ ${product.getDiscountedPrice()} ")
                } else {
                    setStrikedPrice(holder, product)
                }
            } else holder.tvPrice.text = product.getFinalPriceText(context)

            if (AppUtils.getConfig(context).isFree_menu_mapping) {
                if (product.isFree()) {
                    val freeQtyAdded: Int = mainApplication.getFreeQuantityAdded(product)
                    when{
                        freeQtyAdded >= product.getFreeQuantity() -> holder.tvPrice.text =  String.format("\u20B9 %.2f", product.getPrice())
                        product.discountedPrice == 0.0 -> holder.tvPrice.text = AppUtils.getConfig(context).company_paid_text
                        AppUtils.getConfig(context).isHide_discount -> holder.tvPrice.text = "₹ ${product.getDiscountedPrice()}"
                        else -> setStrikedPrice(holder, product)
                    }
                }
            }
//            holder.tvPrice.text = context.getString(R.string.guest_ordering_text)

            holder.clItemBody.setOnClickListener{itemClickAction(product, position, holder)}
            holder.ivItemAdd.setOnClickListener{itemClickAction(product, position, holder)}

            if (product.isProductVeg) {
                holder.ivIsVeg.setImageResource(R.drawable.ic_veg_icon)
            } else {
                holder.ivIsVeg.setImageResource(R.drawable.ic_non_veg)
            }
        }
    }
    private fun itemClickAction(product:Product, position: Int,holder: RecyclerView.ViewHolder){
        try {
            if (product.isRecommended()) {
                EventUtil.FbEventLog(activity, EventUtil.MENU_RECOMMENDED_CLICK, EventUtil.SCREEN_MENU)
                HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_RECOMMENDED_CLICK)
            } else {
                EventUtil.FbEventLog(activity, EventUtil.MENU_ADD_ITEM, EventUtil.SCREEN_MENU)
            }
        } catch (exp: java.lang.Exception) {
            exp.printStackTrace()
        }
        try {
            val jo = JSONObject()
            jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu")
            HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_ITEM_ADD, jo)
        } catch (exp: java.lang.Exception) {
            exp.printStackTrace()
        }

        val vendor = vendors[position-1]
//        if (!vendor.isValid()) {
//            if (activity != null) {
//                AppUtils.showToast("Something went wrong.", true, 0)
////                activity.finish()
//            }
//            return
//        }
        if (mainApplication.cart.orderProducts.size > 0) {
            showRemoveCartItemDialog(product.clone(), vendor, holder)
        }
        else {
            addProductToCart(product.clone(), vendor, holder)
        }

    }
    private fun showRemoveCartItemDialog(product: Product, vendor: Vendor, holder: RecyclerView.ViewHolder) {
        if (activity == null) return
        val generalDialogFragment = GeneralDialogFragment.newInstance("Replace cart item?",
                "All the previous items in the cart will be discarded.", object : GeneralDialogFragment.OnDialogFragmentClickListener {
            override fun onPositiveInteraction(dialog: GeneralDialogFragment) {
                clearLocalOrder()
                addProductToCart(product.clone(), vendor, holder)
            }

            override fun onNegativeInteraction(dialog: GeneralDialogFragment) {
                notifyDataSetChanged()
            }
        })
        generalDialogFragment.show(activity.supportFragmentManager, "dialog")
    }
    private fun clearLocalOrder() {
        mainApplication.clearOrder()
    }
    private fun setStrikedPrice(holder : BookmarkViewHolder, product: Product){
        val oldPrice  = "₹ ${product.getPrice()} "
        val newPrice  = "₹ ${product.getDiscountedPrice()} "
        val sum = SpannableStringBuilder(oldPrice+newPrice)
        sum.setSpan(StrikethroughSpan(), 0, oldPrice.length-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.tvPrice.text = sum
    }

    private fun addProductToCart(product: Product, vendor: Vendor, holder: RecyclerView.ViewHolder) {
        val map = HashMap<String, Any>()
        try {
            map[source] = ApplicationConstants.ADD_ITEM_SOURCE_EXP
            map[is_bookmarked] = if (product.isBookmarked()) "Yes" else "No"
            map[is_trending] = if (product.isTrendingItem()) "Yes" else "No"
            map[userId] = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID)
            map[vendor_id] = vendor.id
            map[menu_item_id] = product.getId()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mainApplication.cart.addProductToCart(product, holder, mainApplication, null, activity as AppCompatActivity?, vendor, occasionId, map)
    }

    override fun getItemViewType(position: Int): Int {
        return if (productList[position] is Product) ITEM_TYPE_PRODUCT else ITEM_TYPE_LABEL
    }

    fun updateBookmarkList( productList:ArrayList<Object>, vendors:ArrayList<Vendor>,occasionId: Long){
        this.productList = productList;
        this.vendors = vendors;
        this.occasionId = occasionId;
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}