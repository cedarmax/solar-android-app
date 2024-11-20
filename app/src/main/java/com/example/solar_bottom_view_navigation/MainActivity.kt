package com.example.solar_bottom_view_navigation

import android.content.Intent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//Firebase firestore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.app.ui.LoginFragment
import com.google.firebase.firestore.FirebaseFirestore
//Login fragment

/*
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        //val navView = findViewById<BottomNavigationView>(R.id.navigation_home)
       // navView.title = "New Title".menu.findItem(R.id.navigation_home).title = "New Title"

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    fun basicReadWrite() {
        // [START write_message]
        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")
        // [END write_message]

        // [START read_message]
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        // [END read_message]
    }
} */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Load LoginFragment if this is the first time launching
        if (savedInstanceState == null) {
            loadFragment(LoginFragment())
        }
        // Function to replace the current fragment

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username == "admin" && password == "password") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        // Reference to the 'user1' document in the 'users' collection
        val userRef = db.collection("test").document("test")

        // Retrieve the document
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Convert document to a User object (this assumes an appropriate data class)
                    val user = document.toObject(User::class.java)
                    Log.d("MainActivity", "User name: ${user?.battery}, age: ${user?.panel}")
                    val batteryString = user?.battery.toString()
                    val panelString = user?.panel.toString()
                    val textView: TextView = findViewById(R.id.textBatteryVoltage) as TextView
                        textView.text = batteryString
                    val textView1: TextView = findViewById(R.id.textPanelVoltage) as TextView
                        textView1.text = panelString
                } else {
                    Log.d("MainActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("MainActivity", "get failed with ", exception)
            }
    }

    private fun loadFragment(fragment: LoginFragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

data class User(
    var battery: Int? = null,
    var panel: Int? = null
)