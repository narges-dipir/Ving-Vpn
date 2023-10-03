package com.abrnoc.application.presentation.viewModel

import android.content.Context
import android.net.Uri
import android.os.RemoteException
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceDataStore
import com.abrnoc.application.connection.database.GroupManager
import com.abrnoc.application.connection.database.ProfileManager
import com.abrnoc.application.connection.group.GroupUpdater
import com.abrnoc.application.connection.group.RawUpdater
import com.abrnoc.application.connection.neko.SagerDatabase
import com.abrnoc.application.connection.neko.Util
import com.abrnoc.application.ftm.AbstractBean
import com.abrnoc.application.ftm.KryoConverters
import com.abrnoc.application.presentation.connection.BaseService
import com.abrnoc.application.presentation.connection.DataStore
import com.abrnoc.application.presentation.connection.GroupType
import com.abrnoc.application.presentation.connection.Key
import com.abrnoc.application.presentation.connection.OnPreferenceDataStoreChangeListener
import com.abrnoc.application.presentation.connection.PackageCache.reload
import com.abrnoc.application.presentation.connection.ProxyEntity
import com.abrnoc.application.presentation.connection.ProxyGroup
import com.abrnoc.application.presentation.connection.SagerConnection
import com.abrnoc.application.presentation.connection.SagerNet
import com.abrnoc.application.presentation.connection.SubscriptionBean
import com.abrnoc.application.presentation.connection.SubscriptionFoundException
import com.abrnoc.application.presentation.connection.SubscriptionType
import com.abrnoc.application.presentation.connection.onMainDispatcher
import com.abrnoc.application.presentation.connection.readableMessage
import com.abrnoc.application.presentation.connection.runOnDefaultDispatcher
import com.abrnoc.application.presentation.viewModel.event.ProxyEvent
import com.abrnoc.application.presentation.viewModel.state.DefaultConfigState
import com.abrnoc.application.repository.model.DefaultConfig
import com.abrnoc.domain.common.Result
import com.abrnoc.domain.connection.GetDefaultConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.nekohasekai.sagernet.aidl.ISagerNetService
import io.nekohasekai.sagernet.aidl.TrafficStats
import io.nekohasekai.sagernet.widget.UndoSnackbarManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DefaultConfigViewModel @Inject constructor(
    private val getDefaultConfigUseCase: GetDefaultConfigUseCase,
    private val context: Context,
) : ViewModel(),
    SagerConnection.Callback,
    OnPreferenceDataStoreChangeListener,
    ProfileManager.Listener,
    GroupManager.Listener,
    UndoSnackbarManager.Interface<ProxyEntity> {
    //    private val _state = MutableStateFlow<DefaultConfigState?>(null)
//    val state: StateFlow<DefaultConfigState?> = _state
//    val defaultConfigFlow: LiveData<DefaultConfigState?> = state.asLiveData()
    var configState by mutableStateOf(DefaultConfigState())
    val connection = SagerConnection(true)

    //    lateinit var proxyGroup: ProxyGroup
    var selected = false
    var configurationIdList: MutableList<Long> = mutableListOf()
    val configurationList = HashMap<Long, ProxyEntity>()
    val select: Boolean = false
    val selectedItem: ProxyEntity? = null
    var groupList: ArrayList<ProxyGroup> = ArrayList()

    init {
        getAllConfigs()
//        reloadProfiles()
        DataStore.profileCacheStore.registerChangeListener(this)
    }

    fun onEvent(event: ProxyEvent) {
        var selectedProfileIndex = -1
        when (event) {
            is ProxyEvent.ConfigEvent -> {
                val profileAccess = Mutex()
                val selectedProxy = event.defaultConfig.id ?: DataStore.selectedProxy
                var update: Boolean
                var lastSelected: Long
                // (ConfigurationFragment.SelectCallback).returnProfile(selectedItem?.id ?: 1L)
                runOnDefaultDispatcher {
                    profileAccess.withLock {
                        update = DataStore.selectedProxy != event.defaultConfig.id
                        lastSelected = DataStore.selectedProxy
                        Timber.i("^^", "timber workes")
                        DataStore.selectedProxy = event.defaultConfig.id ?: 1L
                        println(" ^^^^ ${DataStore.selectedProxy}")
//                    onMainDispatcher {
//                        selectedView.visibility = View.VISIBLE
//                    }
                    }
                }
            }
        }
    }

     fun getAllConfigs() {
        viewModelScope.launch {
            getDefaultConfigUseCase(Unit).collect { result ->
                when (result) {
                    is Result.Error -> {
                        configState = configState.copy(
                            error = result.exception.message ?: "An unexpected error occured",
                        )
                    }

                    Result.Loading -> {
                        configState = configState.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        var id = 1
                        SagerDatabase.proxyDao.reset()
                        SagerDatabase.proxyDao.deleteAllProxyEntities()
                        SagerDatabase.proxyDao.clearPrimaryKey()
                        result.data.forEach { config ->
                            try {
                                var url = ""
                                if (config.protocol == "SSH") {
                                    val stringBuilder = StringBuilder()
                                    stringBuilder.append("ssh://")
                                    stringBuilder.append("&address=").append(config.address)
                                    stringBuilder.append("&port=").append(config.port)
                                    stringBuilder.append("&username=").append(config.username)
                                    stringBuilder.append("&password=").append(config.password)
                                    url = stringBuilder.toString()
                                } else{
                                    url = config.url
                                }
                                val proxies = RawUpdater.parseRaw(url)
                                println(" the proxies are : $proxies")
                                if (proxies.isNullOrEmpty()) {
                                    onMainDispatcher {
                                        Timber.e("Error", "Proxy Not Found")
                                    }
                                } else {
                                    import(proxies)
                                }
                            } catch (e: SubscriptionFoundException) {
                                importSubscription(Uri.parse(e.link))
                            }
//                        reloadProfiles()
                        }
                        configState = configState.copy(
                            configs = result.data.map { it ->
                                DefaultConfig(
                                    (id++).toLong(),
                                    it.address,
                                    it.alpn,
                                    it.country,
                                    it.fingerprint,
                                    it.flag,
                                    it.password,
                                    it.port,
                                    it.protocol,
                                    it.security,
                                    it.sni,
                                    it.type,
                                    it.url
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    suspend fun import(proxies: List<AbstractBean>) {
        val targetId = DataStore.selectedGroupForImport()
        for (proxy in proxies) {
            ProfileManager.createProfile(targetId, proxy)
        }
        onMainDispatcher {
            DataStore.editingGroup = targetId
            Timber.i("proxies", proxies.size)
//            snackbar(
//                this@ConnActivity.resources.getQuantityString(
//                    R.plurals.added, proxies.size, proxies.size
//                )
//            ).show()
        }
    }

    private fun getItem(profileId: Long): ProxyEntity {
        var profile = configurationList[profileId]
        if (profile == null) {
            profile = ProfileManager.getProfile(profileId)
            if (profile != null) {
                configurationList[profileId] = profile
            }
        }
        return profile!!
    }

    private fun getItemAt(index: Int) = getItem(configurationIdList[index])
    suspend fun importSubscription(uri: Uri) {
        val group: ProxyGroup

        val url = uri.getQueryParameter("url")
        if (!url.isNullOrBlank()) {
            group = ProxyGroup(type = GroupType.SUBSCRIPTION)
            val subscription = SubscriptionBean()
            group.subscription = subscription

            // cleartext format
            subscription.link = url
            group.name = uri.getQueryParameter("name")

            val type = uri.getQueryParameter("type")
            when (type?.lowercase()) {
                "sip008" -> {
                    subscription.type = SubscriptionType.SIP008
                }
            }
        } else {
            val data = uri.encodedQuery.takeIf { !it.isNullOrBlank() } ?: return
            try {
                group = KryoConverters.deserialize(
                    ProxyGroup().apply { export = true },
                    Util.zlibDecompress(Util.b64Decode(data))
                ).apply {
                    export = false
                }
            } catch (e: Exception) {
                onMainDispatcher {
//                    alert(e.readableMessage).show()
                    Timber.i("vpns", e.readableMessage)
                }
                return
            }
        }

        val name = group.name.takeIf { !it.isNullOrBlank() } ?: group.subscription?.link
        ?: group.subscription?.token
        if (name.isNullOrBlank()) return

        group.name = group.name.takeIf { !it.isNullOrBlank() }
            ?: ("Subscription #" + System.currentTimeMillis())

        onMainDispatcher {
//            displayFragmentWithId(R.id.nav_group)
            finishImportSubscription(group)
        }
    }

    private suspend fun finishImportSubscription(subscription: ProxyGroup) {
        GroupManager.createGroup(subscription)
        GroupUpdater.startUpdate(subscription, true)
    }

    override fun onPreferenceDataStoreChanged(store: PreferenceDataStore, key: String) {
        when (key) {
            Key.SERVICE_MODE -> onBinderDied()
            Key.PROXY_APPS, Key.BYPASS_MODE, Key.INDIVIDUAL -> {
                if (DataStore.serviceState.canStop) {
//                    snackbar(getString(R.string.restart)).setAction(R.string.apply) {
                    SagerNet.reloadService()
//                    }.show()
                }
            }
        }
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        changeState(state, msg, true)
    }

    override fun onServiceConnected(service: ISagerNetService) = changeState(
        try {
            BaseService.State.values()[service.state]
        } catch (_: RemoteException) {
            BaseService.State.Idle
        }
    )

    private fun changeState(
        state: BaseService.State,
        msg: String? = null,
        animate: Boolean = false,
    ) {
        DataStore.serviceState = state

        if (!DataStore.serviceState.connected) {
            statsUpdated(emptyList())
        }

//        binding.fab.changeState(state, DataStore.serviceState, animate)
//        binding.stats.changeState(state)
//        if (msg != null) snackbar(getString(R.string.vpn_error, msg)).show()

        when (state) {
            BaseService.State.Stopped -> {
                runOnDefaultDispatcher {
                    // refresh view
                    ProfileManager.postUpdate(DataStore.currentProfile)
                }
            }

            else -> {}
        }
    }

    override fun onServiceDisconnected() = changeState(BaseService.State.Idle)
    override fun onBinderDied() {
        connection.disconnect(context = context)
        connection.connect(context, this)
    }

    fun onClickConnect(connect: ActivityResultLauncher<Void?>) {
//         val connect = activityContext.registerForActivityResult(StartService()) {}
        if (DataStore.serviceState.canStop) SagerNet.stopService() else connect.launch(
            null
        )
//        connect.launch(null)
    }

    override suspend fun groupAdd(group: ProxyGroup) = Unit

    override suspend fun groupUpdated(group: ProxyGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun groupUpdated(groupId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun groupRemoved(groupId: Long) = Unit

    override suspend fun onAdd(profile: ProxyEntity) {
        if (groupList.find { it.id == profile.groupId } == null) {
            DataStore.selectedGroup = profile.groupId
            reload()
        }
    }

    override suspend fun onUpdated(profileId: Long, trafficStats: TrafficStats) = Unit
    override suspend fun onUpdated(profile: ProxyEntity) = Unit

    override suspend fun onRemoved(groupId: Long, profileId: Long) {
        val group = groupList.find { it.id == groupId } ?: return
        if (group.ungrouped && SagerDatabase.proxyDao.countByGroup(groupId) == 0L) {
            reload()
        }
    }

    override fun undo(actions: List<Pair<Int, ProxyEntity>>) {
        for ((index, item) in actions) {
//            configurationListView.post {
//                configurationList[item.id] = item
//                configurationIdList.add(index, item.id)
//                notifyItemInserted(index)
//            }
        }
    }

    override fun commit(actions: List<Pair<Int, ProxyEntity>>) {
        val profiles = actions.map { it.second }
        runOnDefaultDispatcher {
            for (entity in profiles) {
                ProfileManager.deleteProfile(entity.groupId, entity.id)
            }
        }
    }

    // ///////
//    fun reloadProfiles() {
//        var newProfiles = SagerDatabase.proxyDao.getByGroup(proxyGroup.id)
//        val subscription = proxyGroup.subscription
//        if (subscription != null) {
//            if (subscription.selectedGroups.isNotEmpty()) {
//                newProfiles =
//                    newProfiles.filter { it.requireBean().group in subscription.selectedGroups }
//            }
//            if (subscription.selectedOwners.isNotEmpty()) {
//                newProfiles =
//                    newProfiles.filter { it.requireBean().owner in subscription.selectedOwners }
//            }
//            if (subscription.selectedTags.isNotEmpty()) {
//                newProfiles = newProfiles.filter { profile ->
//                    profile.requireBean().tags.containsAll(
//                        subscription.selectedTags
//                    )
//                }
//            }
//        }
//        when (proxyGroup.order) {
//            GroupOrder.BY_NAME -> {
//                newProfiles = newProfiles.sortedBy { it.displayName() }
//
//            }
//
//            GroupOrder.BY_DELAY -> {
//                newProfiles =
//                    newProfiles.sortedBy { if (it.status == 1) it.ping else 114514 }
//            }
//        }
//
//        configurationList.clear()
//        configurationList.putAll(newProfiles.associateBy { it.id })
//        val newProfileIds = newProfiles.map { it.id }
//
//        var selectedProfileIndex = -1
//
//        if (selected) {
//            val selectedProxy = selectedItem?.id ?: DataStore.selectedProxy
//            selectedProfileIndex = newProfileIds.indexOf(selectedProxy)
//        }
// //        configurationListView.post {
// //            configurationIdList.clear()
// //            configurationIdList.addAll(newProfileIds)
// //            notifyDataSetChanged()
// //
// //            if (selectedProfileIndex != -1) {
// //                configurationListView.scrollTo(selectedProfileIndex, true)
// //            } else if (newProfiles.isNotEmpty()) {
// //                configurationListView.scrollTo(0, true)
// //            }
// //
// //        }
//    }
}
