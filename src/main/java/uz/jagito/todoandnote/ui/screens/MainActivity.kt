package uz.jagito.todoandnote.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.header_layout_navigation_view.view.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.contracts.ContractTodo
import uz.jagito.todoandnote.data.*
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.data.repositories.TodoRepository
import uz.jagito.todoandnote.ui.adapters.AddLabelDialogAdapter
import uz.jagito.todoandnote.ui.adapters.SearchListAdapter
import uz.jagito.todoandnote.ui.adapters.TodoViewPagerAdapter
import uz.jagito.todoandnote.ui.dialogs.AddLabelDialog
import uz.jagito.todoandnote.ui.dialogs.EditProfileDialog
import uz.jagito.todoandnote.ui.presenters.TodoPresenter
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), ContractTodo.View {
    private val addLabelDialogAdapter = AddLabelDialogAdapter()
    private val searchAdapter = SearchListAdapter()
    private val requestCode = 1
    private lateinit var profileDialog: EditProfileDialog
    private lateinit var viewPagerAdapter: TodoViewPagerAdapter
    private lateinit var presenter: TodoPresenter
    private lateinit var buttonFirstTodo: LinearLayout


    private var permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        buttonFirstTodo = findViewById<LinearLayout>(R.id.addFirstTodo)
        presenter = TodoPresenter(TodoRepository(), this)
        loadViews()
    }

    private fun loadViews() {
        profileDialog = EditProfileDialog(this)
        initFloatingActionButtonMenu()
        loadNavigationView()

        presenter.loadData()

        drawerMenuBtn.setOnClickListener { drawerLayoutMain.openDrawer(GravityCompat.START) }

        searchAdapter.apply {

            setOnClickListener { presenter.clickItemSingleNote(it.id) }

            setOnDeleteListener { presenter.onDelete(it) }

            setOnDoneListener { presenter.onDone(it) }

            setOnCancelListener { presenter.onCancel(it) }

            setOnEditListener { presenter.clickItemSingleNote(it.id) }
        }

        buttonFirstTodo.setOnClickListener {
            openAddTodoActivity(-1)
        }
        buttonFirstTodo.visibility = View.VISIBLE
    }

    private fun loadProfileDialog() {
        profileDialog.apply {

            getUserData { userData ->
                Log.d("checkDialog", "getUserData = $userData")
                presenter.setUserData(userData)
            }
            show()
            setOnChargeAvatar {

                pickImageFromGallery()

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!checkPermission(this@MainActivity, permissions)) {
                        requestPermissions(permissions, PERMISSION_REQUEST)
                    } else {
                        //onImageGalleryClicked(it)

                        pickImageFromGallery()
                    }
                } else {
                   // onImageGalleryClicked(it)
                    pickImageFromGallery()
                }*/


            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    val requestAgain =
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                            permissions[i]
                        )
                    if (requestAgain) {
                        showMessage("Доступ запрещен")
                        if (profileDialog.isShowing) {
                            profileDialog.dismiss()
                        }
                    } else {
                        showMessage("Пожалуйста, включите разрешение хранения в настройках")
                        if (profileDialog.isShowing) {
                            profileDialog.dismiss()
                        }
                    }

                }
            }

        }
    }

    private fun initFloatingActionButtonMenu() {
        multiple_menu.setOnFloatingActionsMenuUpdateListener(object :
            FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
            override fun onMenuExpanded() {
                floating_menu_background.visibility = View.VISIBLE
            }

            override fun onMenuCollapsed() {
                floating_menu_background.visibility = View.GONE
            }
        })

        floating_menu_background.setOnClickListener {
            multiple_menu.collapse()
        }

        actionAddTodo.setOnClickListener {
            presenter.clickAddTodo()
            multiple_menu.collapse()
        }
        actionAddLabel.setOnClickListener {
            presenter.openAddLabelDialog()
            multiple_menu.collapse()
        }
    }

    private fun setViewPagerAdapter(list: List<ListTodoAndLabelData>) {
        viewPagerAdapter = TodoViewPagerAdapter(list as ArrayList<ListTodoAndLabelData>, this)
        pagerMain.adapter = viewPagerAdapter

        viewPagerAdapter.apply {

            setOnItemClickListener { presenter.clickItemSingleNote(it.id) }

            setOnDeleteListener { showConfirm("Delete", it) }

            setOnDoneListener { showConfirm("Done", it) }

            setOnCancelListener { showConfirm("Cancel", it) }

            setOnEditListener { presenter.clickItemSingleNote(it.id) }
        }

        TabLayoutMediator(tabLayoutMain, pagerMain) { tab, position ->
            tab.text = viewPagerAdapter.list[position].labelData.label

        }.attach()

        loadListenerForTabs()
    }

    override fun loadData(data: List<ListTodoAndLabelData>) {
        setViewPagerAdapter(data)
        buttonFirstTodo.visibility =
            if (data[0].listNote.isEmpty()) View.VISIBLE else View.INVISIBLE
    }

    override fun openAddTodoActivity(ID: Long) {
        val intent = Intent(this, TodoItemActivity::class.java)
        intent.putExtra("TODO_ID", ID)
        startActivityForResult(intent, requestCode)
    }

    override fun openAddLabelDialog(list: List<LabelData>) {
        addLabelDialogAdapter.submitList(list)
        val d = AddLabelDialog(this, addLabelDialogAdapter)
        d.show()

        d.setonAddLabelListener { presenter.addLabel(it) }

        d.setonDeleteLabelListener {
            dialogDeleteLabel(it)
        }
    }

    private fun dialogDeleteLabel(label: LabelData) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage("Are you sure delete label and all todo of label ?")
            .setPositiveButton("Ok") { _, _ ->
                showMessage("Delete successful!")
                presenter.deleteLabel(label)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    override fun deleteTodoFromAdapter(todoData: TodoData) {
        viewPagerAdapter.remove(todoData)
        viewPagerAdapter.list.forEachIndexed { _, list ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                list.listNote.removeIf {
                    it.id == todoData.id
                }
            } else {
                while (list.listNote.indexOfFirst { it.id == todoData.id } != -1) {
                    list.listNote.removeAt(list.listNote.indexOfFirst { it.id == todoData.id })
                }
            }
        }
        if (viewPagerAdapter.list[0].listNote.isEmpty()) {
            buttonFirstTodo.visibility = View.VISIBLE
        }
    }

    override fun updateTodoFromAdapter(todoData: TodoData) {
        viewPagerAdapter.update(todoData)
        viewPagerAdapter.list.forEach {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.listNote.replaceAll { t ->
                    if (t.id == todoData.id) todoData else t
                }
            }
        }
    }

    override fun setUserData(userData: UserData) {
        val header = navigationView.getHeaderView(0)
        header.emailAccount.text = userData.email
        header.nameAccount.text = userData.name
        val uri = userData.pathOfAvatarImage.toUri()

        val picasso = Picasso.get()
        picasso.load(uri).rotate(90f).error(R.drawable.ic_account).into(header.imageAccount)


        profileDialog.setUserData(userData)
    }

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission: Int = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }


    override fun addLabelToAdapter(labelData: LabelData) {
        addLabelDialogAdapter.addLabel(labelData)
    }

    override fun deleteLabelFromViewPagerAdapter(labelData: LabelData) {
        addLabelDialogAdapter.deleteLabel(labelData)
        loadListenerForTabs()
    }

    override fun addLabelToViewPagerAdapter(labelData: LabelData) {
        viewPagerAdapter.addPage(labelData)
        loadListenerForTabs()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun onImageGalleryClicked(v: View) {


        // invoke the image gallery using an implict intent.
        val photoPickerIntent = Intent(Intent.ACTION_PICK)

        // where do we want to find the data?
        val pictureDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val pictureDirectoryPath = pictureDirectory.path
        // finally, get a URI representation
        val data = Uri.parse(pictureDirectoryPath)

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*")

        // we will invoke this activity, and get something back from it.
        startActivityForResult(
            photoPickerIntent,
            IMAGE_GALLERY_REQUEST
        )
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 5 && requestCode == this.requestCode) {
            val note = data?.getSerializableExtra("added") as TodoData
            viewPagerAdapter.add(note)
            if (buttonFirstTodo.isVisible) {
                buttonFirstTodo.visibility = View.INVISIBLE
            }
        }
        if (resultCode == 6 && requestCode == this.requestCode) {
            val note = data?.getSerializableExtra("added") as TodoData
            updateTodoFromAdapter(note)
        }
        if (resultCode == 7 && requestCode == this.requestCode) {
            presenter.loadData()
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Toast.makeText(this, "Image Saved.", Toast.LENGTH_LONG).show()
            }
            // if we are here, everything processed successfully.
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                // if we are here, we are hearing back from the image gallery.

                // the address of the image on the SD Card.
                val imageUri = data!!.data

                Log.d("checkDialog", "imageURi = ${imageUri?.path}")
                // declare a stream to read the image data from the SD Card.
                val inputStream: InputStream?

                // we are getting an input stream, based on the URI of the image.
                try {
                    inputStream = contentResolver.openInputStream(imageUri!!)

                    // get a bitmap from the stream.
                    val image = BitmapFactory.decodeStream(inputStream)
                    //imageAccountDialog.setImageBitmap(image)


                    // show the image to the user
                    profileDialog.setImage(imageUri)


                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    // show a message to the user indictating that the image is unavailable.
                    showMessage("Unable to open image")
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                loadSearchList(newText)
                return false
            }
        })
        searchView.setOnCloseListener {
            searchList.visibility = View.GONE
            true
        }
        return true
    }

    fun loadSearchList(d: String) {
        val query = d.toLowerCase(Locale.getDefault())
        val ls = viewPagerAdapter.list[0].listNote
        val sorted = ArrayList<TodoData>()
        ls.forEach {
            if (it.description.toLowerCase(Locale.getDefault()).contains(query)
                || it.hashTag.toLowerCase(Locale.getDefault()).contains(query)
                || it.title.toLowerCase(Locale.getDefault()).contains(query)
            ) {
                sorted.add(it)
            }
        }
        searchAdapter.submitList(sorted)
    }

    private fun loadNavigationView() {
        val menu = navigationView.menu

        val allTodoBtn = menu.findItem(R.id.allTodo)
        val trash = menu.findItem(R.id.trashBtn)
        val share = menu.findItem(R.id.shareBtn)
        val history = menu.findItem(R.id.historyBtn)
        val termsOfConditions = menu.findItem(R.id.termsButton)
        val instruction = menu.findItem(R.id.instructionsButton)
        allTodoBtn.setOnMenuItemClickListener {
            startActivityForResult(Intent(this, AllTodoActivity::class.java), requestCode)
            drawerLayoutMain.closeDrawers()
            true
        }

        trash.setOnMenuItemClickListener {
            startActivityForResult(Intent(this, TrashActivity::class.java), requestCode)
            drawerLayoutMain.closeDrawers()
            true
        }

        share.setOnMenuItemClickListener {
            shareApplication(this)
            drawerLayoutMain.closeDrawers()
            true
        }

        history.setOnMenuItemClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            true
        }

        termsOfConditions.setOnMenuItemClickListener {
            openPdf("terms")
            true
        }

        instruction.setOnMenuItemClickListener {
            openPdf("instruction")
            true
        }

        navigationView.getHeaderView(0).editProfileBtn.setOnClickListener {
            loadProfileDialog()
        }
    }

    private fun shareApplication(context: Context) {
        try {
            val pm = context.packageManager
            val ai = pm.getApplicationInfo(context.packageName, 0)
            val srcFile = File(ai.publicSourceDir)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "*/*"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Developer: Nizamov Shakhzod"
            )
            val uri: Uri = FileProvider.getUriForFile(this, context.packageName, srcFile)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.grantUriPermission(
                context.packageManager.toString(),
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_search) {
            item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    searchList.adapter = searchAdapter
                    searchList.layoutManager = GridLayoutManager(this@MainActivity, 2)
                    searchList.visibility = View.VISIBLE

                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    searchList.visibility = View.GONE
                    return true
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openPdf(key: String) {
        // Open the PDF file from raw folder

        val inputStream =
            resources.openRawResource(if (key == "terms") R.raw.terms_of_conditions else R.raw.instruction)

        // Copy the file to the cache folder
        inputStream.use { inputStream ->
            val file =
                File(cacheDir, if (key == "terms") "terms_of_conditions.pdf" else "instruction.pdf")
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }

        val cacheFile =
            File(cacheDir, if (key == "terms") "terms_of_conditions.pdf" else "instruction.pdf")

        // Get the URI of the cache file from the FileProvider
        val uri = FileProvider.getUriForFile(this, this.packageName, cacheFile)
        if (uri != null) {
            // Create an intent to open the PDF in a third party app
            val pdfViewIntent = Intent(Intent.ACTION_VIEW)
            pdfViewIntent.data = uri
            pdfViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(pdfViewIntent, "Choose PDF viewer"))
        }
    }

    @SuppressLint("RestrictedApi")
    private fun popupMenu(it: View, position: Int) {
        val menuBuilder = MenuBuilder(it.context)
        val menuInflater = MenuInflater(it.context)
        menuInflater.inflate(R.menu.tablayout_menu, menuBuilder)
        val menu = MenuPopupHelper(it.context, menuBuilder, it)
        menu.setForceShowIcon(true)
        menu.show(getScreenResolution(it.context).width / 4, -100)
        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuModeChange(menu: MenuBuilder?) = Unit
            override fun onMenuItemSelected(menu: MenuBuilder?, item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.actionDeleteTabLayout -> {
                        val labelData = viewPagerAdapter.list[position].labelData
                        dialogDeleteLabel(labelData)
                    }
                }
                return true
            }
        })
    }

    private fun getScreenResolution(context: Context): ScreenSize {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        return ScreenSize(width, height)
    }

    private fun loadListenerForTabs() {
        val tabCount = tabLayoutMain.tabCount
        // start from 2 because user can not delete "ALL" and "TODAY" tabs
        for (i in 2..tabCount) {
            val tab = tabLayoutMain.getTabAt(i)
            tab?.view?.setOnLongClickListener {
                popupMenu(it, i)
                true
            }
        }
    }

    private fun showConfirm(key: String, todoData: TodoData) {
        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage("Are you sure ${key.toLowerCase(Locale.getDefault())} todo?")
            .setPositiveButton(key) { _, _ ->
                showMessage("$key successful!")
                when (key) {
                    "Delete" -> {
                        presenter.onDelete(todoData)
                    }
                    "Cancel" -> {
                        presenter.onCancel(todoData)
                    }
                    "Done" -> {
                        presenter.onDone(todoData)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun checkPermission(content: Context, permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (content.checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess

    }


    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return image
    }
}

