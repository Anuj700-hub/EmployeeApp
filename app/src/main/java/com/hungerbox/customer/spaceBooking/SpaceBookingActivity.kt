package com.hungerbox.customer.spaceBooking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.R
import com.hungerbox.customer.config.Config
import com.hungerbox.customer.databinding.ActivitySpaceBookingBinding
import com.hungerbox.customer.model.*
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity
import com.hungerbox.customer.order.listeners.VendorValidationListener
import com.hungerbox.customer.spaceBooking.adapter.DateAdapter
import com.hungerbox.customer.spaceBooking.adapter.SpaceAdapter
import com.hungerbox.customer.spaceBooking.listeners.RecyclerViewClickListener
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.ImageHandling
import com.hungerbox.customer.util.SharedPrefUtil
import com.hungerbox.customer.util.view.GenericPopUpFragment
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SpaceBookingActivity : AppCompatActivity(), VendorValidationListener{
    private var typeOfSpace: String? = null
    private lateinit var viewModel : SpaceBookingViewModel
    private lateinit var binding: ActivitySpaceBookingBinding
    private var dateAdapter = DateAdapter()
    private var spaceAdapter = SpaceAdapter()
    private var lastSelectedDate = 0
    private var lastSelectedSlot = 0
    private var config : Config.SpaceType? = null
    private var noOfGuestList : List<Int>? = null
    private var autoSelectLocationId : Long = -1
    private var dateList : List<SpaceBookingDate> = ArrayList()
    private var errorPopUp : GenericPopUpFragment? = null

    private lateinit  var cityListener : AdapterView.OnItemSelectedListener
    private lateinit  var buildingListener : AdapterView.OnItemSelectedListener
    private lateinit  var floorListener : AdapterView.OnItemSelectedListener
    private lateinit  var slotListener : AdapterView.OnItemSelectedListener
    private lateinit  var recyclerViewClickListener: RecyclerViewClickListener
    private lateinit  var emptyListener: RecyclerViewClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
                this,R.layout.activity_space_booking
        )
        binding.setLifecycleOwner { this.lifecycle }
        initData()

    }
    private fun initData(){
        // intent data
        typeOfSpace = intent.getStringExtra(ApplicationConstants.SpaceBooking.SPACE_TYPE)
        autoSelectLocationId = intent.getLongExtra(ApplicationConstants.SpaceBooking.AUTO_SELECT_LOCATION_ID, -1)
        //space booking config
        config = AppUtils.getSpaceType(this, typeOfSpace)
        //default values
        val cityId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_CITY_ID, -1)

        //view model and its dependencies
        val spaceBookingRepository : SpaceBookingRepository = SpaceBookingRepositoryImpl(this.applicationContext)
        val factory = SpaceBookingViewModelFactory(spaceBookingRepository,autoSelectLocationId, cityId)
        viewModel = ViewModelProviders.of(this,factory).get(SpaceBookingViewModel::class.java)
        binding.viewmodel = viewModel

        setSupportActionBar(binding.tbSpaceBooking)
        binding.ivBack.setOnClickListener { onBackPressed() }
        binding.tvToolbarTitle.text = config?.toolbar_header ?:"Book"//change
        binding.tvSpaceType.text = config?.name
        binding.tvHelperText.text = config?.description
        ImageHandling.loadRemoteImage(config?.icon_url, binding.ivSpaceImage,R.drawable.ic_table_booking_icon_big, R.drawable.ic_table_booking_icon_big, this)
        binding.tvAvaiableSpaceTitle.text = config?.available_spaces_text

        //horizontal recyclerview for date
        binding.rvDates.adapter = dateAdapter

        recyclerViewClickListener = object  : RecyclerViewClickListener{
            override fun onItemClicked(position: Int) {
                viewModel.selectDate(position,true)
                dateAdapter.notifyItemChanged(lastSelectedDate)
                dateAdapter.notifyItemChanged(position)
                lastSelectedDate = position
            }

        }

        emptyListener = object  : RecyclerViewClickListener{
            override fun onItemClicked(position: Int) {

            }

        }
        //vertical recyclerview for space
        binding.rvAvailableSpaces.adapter = spaceAdapter
        spaceAdapter.setClickListener(object  : RecyclerViewClickListener{
            override fun onItemClicked(position: Int) {

                viewModel.selectSpace(position)
                spaceAdapter.notifyItemChanged(lastSelectedSlot)
                spaceAdapter.notifyItemChanged(position)
                lastSelectedSlot = position
            }

        })

        binding.btProceed.setOnClickListener { prepareCartAndMove() } // proceed button click action
        binding.ivCalendar.setOnClickListener {calendarClickAction()}


        observeFromViewModel()
        viewModel.checkForOccasions()

    }
    private fun autoSelectLocation(){
        if (autoSelectLocationId > -1){
            viewModel.findLocationById(autoSelectLocationId)
            autoSelectFirstDate()
        }
    }
    private fun observeFromViewModel(){

        viewModel.isOccasionAvailable.observe(this, Observer {
            if(it){
                getListLocationWithAddress()
                getSlotList()
                updatePeopleSpinner()
            }
        })

        cityListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectCity(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        buildingListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectBuilding(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        floorListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectFloor(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        slotListener = object :  AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectSlot(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        viewModel.selectedCityPos.observe(this, Observer {
            assignListeners(0)
            binding.spCity.setSelection(it)
        })

        viewModel.setListenerLiveData.observe(this, Observer {
            if(it){
                dateAdapter.setClickListener(recyclerViewClickListener)
            }
            else{
                dateAdapter.setClickListener(emptyListener)
            }

        })

        viewModel.selectedBuildingPos.observe(this, Observer {
            assignListeners(1)
            binding.spBuilding.setSelection(it)
        })

        viewModel.selectedFloorPos.observe(this, Observer {
            assignListeners(2)
            binding.spFloor.setSelection(it)
        })

        viewModel.selectedSlotPos.observe(this, Observer {
            binding.spTimeSlot.onItemSelectedListener = slotListener
            binding.spTimeSlot.setSelection(it)
        })

        viewModel.cityListLiveData.observe(this, Observer {
            it?.let {
                updateCitySpinner(it)
            }
        })

        viewModel.buildingListLiveData.observe(this, Observer {
            it?.let {
                updateBuildingSpinner(it)
            }
        })

        viewModel.floorListLiveData.observe(this, Observer {
            it?.let {
                updateFloorSpinner(it)
            }
        })

        viewModel.setEmptyTimeSlotSpinner.observe(this, Observer {
            it?.let {
                setEmptyTimeSlotSpinner(it)
            }
        })

        viewModel.dateLiveData.observe(this, Observer {
            it?.let {
                updateTimeSlotSpinner(it.slots)
            }
        })

        viewModel.showPopup.observe(this, Observer {
            val message = it.first.replace("[spacetype]", config?.space_name?.toLowerCase() ?: "space")
            it?.let {
                if (errorPopUp!=null){
                    errorPopUp!!.dismiss()
                }
                errorPopUp = GenericPopUpFragment
                        .newInstance(message, "OK", true, object : GenericPopUpFragment.OnFragmentInteractionListener {
                            override fun onPositiveInteraction() {
                                if(it.second){
                                    this@SpaceBookingActivity.finish()
                                }
                            }

                            override fun onNegativeInteraction() {

                            }
                        })

                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().add(errorPopUp!!, "Error Popup").commitAllowingStateLoss()
            }
        })

        viewModel.slotListResponse.observe(this, Observer { resource ->
            when(resource){
                is Resource.Success ->{
                    dateList = resource.data!!
                    dateAdapter.setData(dateList)
                    lastSelectedDate = 0
                    autoSelectFirstDate()
                }
                is Resource.Loading ->{
                    //loading
                }
                else -> Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
            }

        })

        viewModel.slotSpaceResponse.observe(this, Observer {
            when(it){
                is Resource.Success ->{
                    lastSelectedSlot = 0
                    spaceAdapter.setData(it.data!!)
                }
                is Resource.Loading ->{
                    //loading
                }
                else -> Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun assignListeners(pos : Int){
        when (pos) {
            0 -> {
                binding.spCity.onItemSelectedListener = cityListener
                binding.spBuilding.onItemSelectedListener = null
                binding.spFloor.onItemSelectedListener = null
            }
            1 -> {
                binding.spCity.onItemSelectedListener = cityListener
                binding.spBuilding.onItemSelectedListener = buildingListener
                binding.spFloor.onItemSelectedListener = null
            }
            2 -> {
                binding.spCity.onItemSelectedListener = cityListener
                binding.spBuilding.onItemSelectedListener = buildingListener
                binding.spFloor.onItemSelectedListener = floorListener
            }
        }
    }

    private fun getListLocationWithAddress() {
        typeOfSpace?.let {
            viewModel.getListLocationWithAddress(it)
        }
        viewModel.companyResponse.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

    }

    private fun getSlotList() {

        viewModel.slotListResponse.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    val res = it
                }
            }
        })
    }

    private fun updateCitySpinner(cityList: List<SpaceBookingCity>){
        val cityAdapter = ArrayAdapter<SpaceBookingCity>(this, R.layout.spinner_bold_text_layout, cityList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCity.adapter = cityAdapter
    }

    private fun updateBuildingSpinner(buildingList: List<SpaceBookingBuilding>){
        val cityAdapter = ArrayAdapter<SpaceBookingBuilding>(this, R.layout.spinner_bold_text_layout, buildingList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spBuilding.adapter = cityAdapter
    }

    private fun updateFloorSpinner(floorList: List<SpaceBookingFloor>){
        val cityAdapter = ArrayAdapter<SpaceBookingFloor>(this, R.layout.spinner_bold_text_layout, floorList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFloor.adapter = cityAdapter
    }

    private fun updateTimeSlotSpinner(data: List<SpaceBookingSlot>){
        val cityAdapter = ArrayAdapter<SpaceBookingSlot>(this, R.layout.spinner_bold_text_layout, data)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTimeSlot.adapter = cityAdapter
        if(!data.isNullOrEmpty()) {
            viewModel.selectedSlotPos.postValue(0)
        }

    }

    private fun setEmptyTimeSlotSpinner(isDisable: Boolean){
        if(isDisable){
            val cityAdapter = ArrayAdapter<String>(this, R.layout.spinner_bold_text_layout, listOf("Time Slot"))
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spTimeSlot.adapter = cityAdapter
        }
        binding.spTimeSlot.isEnabled = !isDisable
    }

    private fun updatePeopleSpinner(){

        var min = config?.guest_min
        var max = config?.guest_max

        if (min == null || min<1){
            min = 1
        }

        if (max == null || max<1){
            max = 1
        }
        val range = if (max > min){ min..max} else{max..min}
        noOfGuestList = range.toList()

        val cityAdapter = ArrayAdapter<Int>(this, R.layout.spinner_bold_text_layout, noOfGuestList!!)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spGuest.adapter = cityAdapter
    }

    private fun autoSelectFirstDate(){
        val runnable :Runnable =  Runnable {
            val viewHolder = binding.rvDates.findViewHolderForAdapterPosition(0)
            viewHolder?.let {
                it.itemView.performClick()
            } }
        Handler().postDelayed(runnable, 500)
    }

    private fun autoScrollAndSelectDate(position : Int){
        val runnable :Runnable =  Runnable {
            val viewHolder = binding.rvDates.findViewHolderForAdapterPosition(position)
            viewHolder?.let {
                it.itemView.performClick()
            } }
        binding.rvDates.smoothScrollToPosition(position)
        Handler().postDelayed(runnable, 700)
    }

    private fun prepareCartAndMove(){
        if (viewModel.selectedVendor==null){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            return
        }
        if (viewModel.selectedSpace==null){
            Toast.makeText(this, "Please select any space", Toast.LENGTH_SHORT).show()
            return
        }
        val noOfGuest = noOfGuestList!![viewModel.selectedNoOfPeoplePos.value!!]
        if (noOfGuest > viewModel.selectedSpace!!.maxQty){
            Toast.makeText(this, config?.max_qty_error, Toast.LENGTH_SHORT).show()
            return
        }
        val application: MainApplication = this.application as MainApplication
        application.clearOrder()
        val cart = application.cart
        cart.showedCartErrorPopup = false

        val vendor  = viewModel.selectedVendor
        vendor?.cart_error = config?.cart_error

        for(i in 1..noOfGuest){
            cart.addProductToCart(viewModel.selectedSpace!!, null, application, this, this,viewModel.selectedVendor, MainApplication.selectedOcassionId, HashMap())
            if (cart.showedCartErrorPopup){
                break
            }
        }
        if (!cart.showedCartErrorPopup) {
            startActivity(getCartIntent(noOfGuest))
        }
    }

    private fun getCartIntent(noOfGuest : Int) : Intent {
        config?.let {
            if (it.guest_details!=null && noOfGuest>1){
                val intent = Intent(this, SpaceGuestActivity::class.java)
                intent.putExtra("guest_size", noOfGuest)
                intent.putExtra("type",typeOfSpace )
                intent.putExtra(ApplicationConstants.SPACE_LOCATION_ID,viewModel.selectedFloor?.id)
                intent.putExtra(ApplicationConstants.SPACE_LOCATION_NAME,viewModel.selectedFloor?.name+","+viewModel.selectedBuilding?.name+","+viewModel.city?.name)
                return intent
            }
        }
        val intent = Intent(this, BookmarkPaymentActivity::class.java)
        intent.putExtra("guest_size", noOfGuest)
        intent.putExtra("type",typeOfSpace )
        intent.putExtra(ApplicationConstants.FROM_SPACE_BOOKING,true)
        intent.putExtra(ApplicationConstants.SPACE_LOCATION_ID,viewModel.selectedFloor?.id)
        intent.putExtra(ApplicationConstants.SPACE_LOCATION_NAME,viewModel.selectedFloor?.name+","+viewModel.selectedBuilding?.name+","+viewModel.city?.name)
        return intent
    }
    private fun calendarClickAction(){
        viewModel.dateListObject?.run {
            val calendar = getCalendarArray()
            val datePicker = DatePickerDialog.newInstance {
                view, year, monthOfYear, dayOfMonth -> scrollToSelectedDate(year, monthOfYear, dayOfMonth)

            }
            datePicker.selectableDays = calendar
            datePicker.show(supportFragmentManager, "datepicker")
        }
    }

    private fun scrollToSelectedDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {

        val date = SimpleDateFormat("yyyy-MM-dd").parse("$year-${monthOfYear+1}-$dayOfMonth")
        val spaceBookingDate = SpaceBookingDate(date.time, "", "", "")
        val foundDateIndex = dateList.binarySearch(spaceBookingDate)
        if (foundDateIndex>=0){
            autoScrollAndSelectDate(foundDateIndex)
        }
    }


    override fun validateAndAddProduct(vendor: Vendor?, product: Product?, isBuffet: Boolean) {

    }

    override fun onBackPressed() {
        MainApplication.clearCart()
        super.onBackPressed()
    }
}