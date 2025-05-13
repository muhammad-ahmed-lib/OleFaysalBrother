package ae.oleapp.employee.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ae.oleapp.R
import ae.oleapp.employee.ui.addemployee.AddEmployeeVM
//import ae.oleapp.employee.ui.addemployee.AddEmployeeVM
import ae.oleapp.employee.ui.employeelist.EmployeeListVM
import ae.oleapp.employee.utils.findNavController
import org.koin.android.ext.android.inject

class EmployeeActivity : AppCompatActivity() {

    val viewModel : AddEmployeeVM by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val clubId = intent.getStringExtra("club_id")
        val navController = findNavController(R.id.nav_host_fragment)
        val graph = navController.navInflater.inflate(R.navigation.app_navigation)
        val args = Bundle().apply {
            putInt("clubId" , clubId?.toInt() ?: 0)
        }
        graph.setStartDestination(R.id.employeeListFragment)
        navController.setGraph(graph , args)

    }
}