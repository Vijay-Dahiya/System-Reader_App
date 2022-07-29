package com.vijay.systemreader

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.vijay.systemreader.databinding.ActivityAppsNameBinding


class AppsName : AppCompatActivity() {
    private lateinit var binding : ActivityAppsNameBinding
    private var appsName = StringBuilder("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppsNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAppsName()
        //getInstalledApps(this)
        binding.packName.setOnClickListener {
            binding.textView.text = appsName
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getAppsName() {
        val pm = packageManager
        val packList = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        for (packageInfo in packList) {
            appsName.append(
                """
Installed package :--  ${packageInfo.packageName}
diffrent :-- ${packageInfo.applicationInfo.loadLabel(getPackageManager()).toString()}
Launch Activity :--  ${pm.getLaunchIntentForPackage(packageInfo.packageName)}""")
            appsName.append("\n----------------------------------")
        }

    }

    fun getInstalledApps(ctx: Context): Set<PackageInfo>? {
        val packageManager: PackageManager = ctx.getPackageManager()
        val allInstalledPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        val filteredPackages: MutableSet<PackageInfo> = HashSet()
        val defaultActivityIcon = packageManager.defaultActivityIcon
        for (each in allInstalledPackages) {
            if (ctx.getPackageName().equals(each.packageName)) {
                continue // skip own app
            }
            try {
// add only apps with application icon
                val intentOfStartActivity =
                    packageManager.getLaunchIntentForPackage(each.packageName)
                        ?: continue
                val applicationIcon = packageManager.getActivityIcon(intentOfStartActivity)
                if (defaultActivityIcon != applicationIcon) {
                    filteredPackages.add(each)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.i("MyTag", "Unknown package name " + each.packageName)
            }
        }
        appsName.append(filteredPackages.toString().toString())
        binding.textView.text = filteredPackages.toString()
        Log.d("istalled_app",filteredPackages.toString())
        return filteredPackages
    }


}