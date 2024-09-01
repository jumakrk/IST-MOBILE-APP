package com.example.istapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(){

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Red,
        contentColor = Color.White
    )

    var passwordVisible by remember { mutableStateOf(false) }
    var passwordText by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.ist_logo), contentDescription ="IST Logo",
            modifier = Modifier.size(150.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Welcome Back",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "Welcome to the IST App. Please login to continue",
            fontSize = 16.sp,
            color = Color.Gray,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange ={email = it},
            label = { Text(text = "Email")},
            modifier = Modifier.width(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Change icon according to the password state
        val icon = if (passwordVisible) {
            R.drawable.hide_icon
        } else {
            R.drawable.show_icon
        }

        OutlinedTextField(value = passwordText, onValueChange ={passwordText = it},
            label = { Text(text = "Password")},
            modifier = Modifier.width(300.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { Icon(painter = painterResource(id = icon),
                contentDescription =  if (passwordVisible) "Hide Password" else "Show Password",
                modifier = Modifier
                    .size(16.dp)
                    .clickable {passwordVisible = !passwordVisible})
            })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /*TODO*/ },
            colors = buttonColors,
            modifier = Modifier.width(120.dp),
            ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Forgot Password?",
            Modifier.clickable {},
            color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "Don't have an account? ",
                color = Color.Black,
                fontWeight = FontWeight.Bold)

            Text(text = "Sign Up",
                Modifier.clickable {},
                color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "or",
            color = Color.Black,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Sign in with",
            color = Color.Black,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Image(painter = painterResource(id = R.drawable.facebook_logo), contentDescription ="Google Logo",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        // Handle Facebook login
                    })

            Image(painter = painterResource(id = R.drawable.google_logo), contentDescription ="Facebook Logo",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        // Handle Google login
                    })

            Image(painter = painterResource(id = R.drawable.twitter_logo), contentDescription ="Twitter Logo",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        // Handle Twitter login
                    })

        }
    }
}