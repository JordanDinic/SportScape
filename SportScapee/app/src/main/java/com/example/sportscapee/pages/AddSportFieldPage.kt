package com.example.sportscapee.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sportscapee.database.DbService
import com.example.sportscapee.view_models.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSportFieldPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel){
    val context = LocalContext.current

    val db = DbService()

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Fudbalski") }
    var description by remember { mutableStateOf("") }
    val imageUris = remember { mutableStateListOf<Uri>() }




    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUris.add(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dodavanje Sportskog Objekta", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = {
                androidx.compose.material3.Text(
                    "Username",
                    color = Color.DarkGray.copy(0.75f)
                )
            })

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            expanded = true,
            onDismissRequest = { },
        ) {
            listOf("Fudbalski", "Teniski", "Košarkaški").forEach { item ->
                DropdownMenuItem(onClick = { type = item }) {
                    Text(item)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { androidx.compose.material3.Text("Username", color = Color.DarkGray.copy(0.75f)) })

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Dodaj slike")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val userId = authViewModel.getCurrentUserId()
            if (userId != null) {
                db.addSportField(
                    name = name,
                    type = type,
                    description = description,
                    createdBy = userId,
                    images = imageUris
                ) { success ->
                    if (success) {
                        Toast.makeText(context, "Objekat uspešno dodat", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Greška pri dodavanju objekta", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Korisnik nije prijavljen", Toast.LENGTH_LONG).show()
            }
        }) {
            Text("Sačuvaj")
        }
    }
}