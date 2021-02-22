package test

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hungerbox.customer.bluetooth.Model.UserViolation
import com.hungerbox.customer.config.Config
import com.hungerbox.customer.mvvm.listeners.*
import com.hungerbox.customer.mvvm.repository.MyAccountRepository
import com.hungerbox.customer.mvvm.util.MyAccountUtil
import com.hungerbox.customer.mvvm.util.MyAccountUtilImpl
import com.hungerbox.customer.mvvm.viewmodel.MYAccountViewModel
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.collections.ArrayList

@RunWith(MockitoJUnitRunner::class)
class MYAccountViewModelTest {
    @Rule @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    lateinit var myAccountUtil : MyAccountUtil
    lateinit var config: Config
    lateinit var viewModel: MYAccountViewModel

    @Before
    fun setUp() {
        config = Mockito.mock(Config::class.java)
        myAccountUtil = Mockito.mock(MyAccountUtil::class.java)
        Mockito.`when`(myAccountUtil.getUserId()).thenReturn("1234")

    }
    @Test
    fun `settings card view should not be visible if disabled from config`(){
        Mockito.`when`(myAccountUtil.isUserSettingVisible()).thenReturn(false)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.btnCVSettingVisibility.value == View.GONE)
    }
    @Test
    fun `settings card view should be visible if enabled from config`(){
        Mockito.`when`(myAccountUtil.isUserSettingVisible()).thenReturn(true)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.btnCVSettingVisibility.value == View.VISIBLE)
    }
    @Test
    fun `change password button should not be visible if ssologinonly is true in config`(){
        Mockito.`when`(myAccountUtil.isSsoLoginOnly()).thenReturn(true)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.btnChangePassVisibility.value == View.GONE)
    }
    @Test
    fun `change password button should be visible if ssologinonly is false in config`(){
        Mockito.`when`(myAccountUtil.isSsoLoginOnly()).thenReturn(false)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.btnChangePassVisibility.value == View.VISIBLE)
    }

    @Test //testing myaccountutil itself will be a better judge
    fun `pip switch should not be visible if conditions are false`(){
        Mockito.`when`(myAccountUtil.isPipEnabled()).thenReturn(false)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.pipSettingVisibility.value == View.GONE)
    }
    @Test //testing myaccountutil itself will be a better judge
    fun `pip switch should be visible if conditions are true`(){
        Mockito.`when`(myAccountUtil.isPipEnabled()).thenReturn(true)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.pipSettingVisibility.value == View.VISIBLE)
    }
    @Test
    fun `desk reference hint text should be same as provided from config`(){
        Mockito.`when`(myAccountUtil.getDeskReferenceHintString()).thenReturn("Rishav")
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.etDeskReferenceHint.value == "Rishav")
    }
    @Test
    fun `log switch should be visible for debug builds`(){
        Mockito.`when`(myAccountUtil.isLogEnabled()).thenReturn(true)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.logSettingVisibility.value == View.VISIBLE)
    }
    @Test
    fun `pip switch text should be same as provided from config`(){
        Mockito.`when`(myAccountUtil.getPipSettingString()).thenReturn("Rishav")
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.pipSettingValue.value == "Rishav")
    }

    @Test
    fun `Proximity card view should not be visible is Bluetooth distancing is not active`(){
        Mockito.`when`(myAccountUtil.isBluetoothDistancingActive()).thenReturn(false)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.cvProximityOptionsVisibility.value == View.GONE)
    }
    @Test
    fun `Proximity card view should not be visible is Bluetooth distancing is active but proximity switch value is 0 from config and share option is disabled and delete option is disabled from config`(){
        Mockito.`when`(myAccountUtil.isBluetoothDistancingActive()).thenReturn(true)
        Mockito.`when`(myAccountUtil.getProximitySwitchValue()).thenReturn(0)
        Mockito.`when`(myAccountUtil.isShareOptionEnabled()).thenReturn(false)
        Mockito.`when`(myAccountUtil.isDeleteOptionEnabled()).thenReturn(false)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.cvProximityOptionsVisibility.value == View.GONE)
    }
    @Test
    fun `Proximity card view should be visible if proximity switch value is enabled from config and bluetooth distancing is enabled`(){
        Mockito.`when`(myAccountUtil.isBluetoothDistancingActive()).thenReturn(true)
        Mockito.`when`(myAccountUtil.getProximitySwitchValue()).thenReturn(1)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.cvProximityOptionsVisibility.value == View.VISIBLE)
    }
    @Test
    fun `Proximity Switch should be visible if proximity switch value is enabled from config and bluetooth distancing is enabled`(){
        Mockito.`when`(myAccountUtil.isBluetoothDistancingActive()).thenReturn(true)
        Mockito.`when`(myAccountUtil.getProximitySwitchValue()).thenReturn(1)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.proximitySwitchVisibility.value == View.VISIBLE)
    }
    @Test
    fun `Proximity card view should be visible if share option is enabled is enabled from config and bluetooth distancing is enabled`(){
        Mockito.`when`(myAccountUtil.isBluetoothDistancingActive()).thenReturn(true)
        Mockito.`when`(myAccountUtil.isShareOptionEnabled()).thenReturn(true)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.cvProximityOptionsVisibility.value == View.VISIBLE)
    }
    @Test
    fun `Share button should be visible if share option is enabled is enabled from config and bluetooth distancing is enabled`(){
        Mockito.`when`(myAccountUtil.isBluetoothDistancingActive()).thenReturn(true)
        Mockito.`when`(myAccountUtil.isShareOptionEnabled()).thenReturn(true)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.shareOptionVisibility.value == View.VISIBLE)
    }
    @Test
    fun `Gender selection option should be visible if ask_gender_info is true in config`(){
        Mockito.`when`(myAccountUtil.isGenderInfoRequired()).thenReturn(true)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.genderSectionVisibility.value == View.VISIBLE)
    }
    @Test
    fun `Delete button should be visible if delete option is enabled from config and bluetooth distancing is enabled`(){
        Mockito.`when`(myAccountUtil.isBluetoothDistancingActive()).thenReturn(true)
        Mockito.`when`(myAccountUtil.isDeleteOptionEnabled()).thenReturn(true)
        viewModel = MYAccountViewModel(TestRepository(),myAccountUtil)
        assertTrue(viewModel.deleteOptionVisibility.value == View.VISIBLE)
    }

    class TestRepository : MyAccountRepository{
        override fun updateUserSetting(key: String, isChecked: Boolean, userSettingListener: UserSettingsListener, apiSignature: String) {

        }

        override fun updateUserSetting(key: String, isChecked: Boolean, userSettingListener: UserSettingsListener) {

        }

        override fun fetchUserSetting(userSettingResponseListener: UserSettingResponseListener, apiSignature: String) {

        }

        override fun fetchUserSetting(userSettingResponseListener: UserSettingResponseListener) {
            userSettingResponseListener.onError()
        }

        override fun fetchUserDetail(userId: String, userDetailResponseListener: UserDetailResponseListener, apiSignature: String) {
            userDetailResponseListener.onError()
        }

        override fun sendContactTracingData(userViolations: List<UserViolation>, contactTracingResponseListener: ContactTracingResponseListener, apiSignature: String) {

        }

        override fun sendContactTracingData(userViolations: List<UserViolation>, contactTracingResponseListener: ContactTracingResponseListener) {

        }

        override fun getViolationsFromDb(): List<UserViolation> {
            val array = ArrayList<UserViolation>()
            return array
        }

        override fun deleteAllViolationsFromDb() {

        }

        override fun updateGenderOnServer(gender: String, updateUserInfoResponseListener: UpdateUserInfoResponseListener, apiSignature: String) {

        }

        override fun updateGenderOnServer(gender: String, updateUserInfoResponseListener: UpdateUserInfoResponseListener) {

        }

        override fun getGendersList(): ArrayList<String> {
            return java.util.ArrayList(listOf("Select", "Male", "Female", "Non-Binary"))
        }

    }
//    class TestUtil: MyAccountUtil{
//        override fun updateEmailSettingValue(isChecked: Boolean) {
//            TODO("Not yet implemented")
//        }
//
//        override fun updateSmsSettingValue(isChecked: Boolean) {
//            TODO("Not yet implemented")
//        }
//
//        override fun updateGeneralNotificationSettingValue(isChecked: Boolean) {
//            TODO("Not yet implemented")
//        }
//
//        override fun updateAppNotificationSettingValue(isChecked: Boolean) {
//            TODO("Not yet implemented")
//        }
//
//        override fun updateChatHeadSettingValue(isChecked: Boolean) {
//            TODO("Not yet implemented")
//        }
//
//        override fun updateGenericSettingValue(key: String, isChecked: Boolean) {
//            TODO("Not yet implemented")
//        }
//
//        override fun isSmsSettingChecked(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isAppNotificationSettingChecked(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isPipSettingChecked(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isLogSettingChecked(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun getUserId(): String {
//            TODO("Not yet implemented")
//        }
//
//        override fun getDeskOrderingStatus(): Int {
//            TODO("Not yet implemented")
//        }
//
//        override fun isPipEnabled(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun getPipSettingString(): String {
//            TODO("Not yet implemented")
//        }
//
//        override fun getDeskReferenceHintString(): String {
//            TODO("Not yet implemented")
//        }
//
//        override fun isLogEnabled(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isSsoLoginOnly(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isUserSettingVisible(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun getProximitySwitchValue(): Int {
//            TODO("Not yet implemented")
//        }
//
//        override fun isShareOptionEnabled(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isDeleteOptionEnabled(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isProximitySwitchOn(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun getContactTracingMaxDays(): Int {
//            TODO("Not yet implemented")
//        }
//
//        override fun updateBLEStartTime() {
//            TODO("Not yet implemented")
//        }
//
//        override fun updateBLEProximityStatus(status: Boolean) {
//            TODO("Not yet implemented")
//        }
//
//        override fun getTrackingStartTimeEndTime(): String {
//            TODO("Not yet implemented")
//        }
//
//        override fun getShiftDuration(): Int {
//            TODO("Not yet implemented")
//        }
//
//        override fun isGenderInfoRequired(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isBluetoothDistancingActive(): Boolean {
//
//        }
//
//        override fun shouldEnableBluetoothForAllDay(): Boolean {
//
//        }
//
//        override fun isPipEnabledFromConfig(): Boolean = true
//
//        override fun isCafeApp(): Boolean = false
//
//        override fun isPipSupportedBySystem(): Boolean = true
//
//        override fun getAndroidVersion(): Int = 24
//
//    }
}