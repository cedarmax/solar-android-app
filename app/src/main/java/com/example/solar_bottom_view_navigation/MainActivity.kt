package com.example.solar_bottom_view_navigation
/*
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.solar_bottom_view_navigation.databinding.ActivityMainBinding
*/
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//Firebase firestore
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

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
}

data class User(
    var battery: Int? = null,
    var panel: Int? = null
)