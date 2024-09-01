package com.example.istapp.screens

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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istapp.R
import com.example.istapp.nav.Routes


@Composable
fun SignupScreen(navController: NavController){

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Red,
        contentColor = Color.White
    )

    var passwordVisible by remember { mutableStateOf(false) }
    var passwordText by remember { mutableStateOf("") }
    var passwordIsFocused by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var emailIsFocused by remember { mutableStateOf(false) }

    // FocusRequester to handle the focus state
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.ist_logo), contentDescription ="IST Logo",
            modifier = Modifier.size(150.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Welcome",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "Please Create an account to continue",
            fontSize = 16.sp,
            color = Color.Gray,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange ={email = it},
            label = {
                Text(
                    text = "Email",
                    color = if (emailIsFocused) Color.Red else Color.Gray
                )},
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Red,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Red
            ),
            modifier = Modifier
                .width(300.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    emailIsFocused = focusState.isFocused
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Change icon according to the password state
        val icon = if (passwordVisible) {
            R.drawable.hide_icon
        } else {
            R.drawable.show_icon
        }

        OutlinedTextField(value = passwordText, onValueChange ={passwordText = it},
            label = {
                Text(
                    text = "Password",
                    color = if (passwordIsFocused) Color.Red else Color.Gray
                )},
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Red,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Red
            ),
            modifier = Modifier
                .width(300.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    passwordIsFocused = focusState.isFocused
                },
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
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "Already have an account? ",
                color = Color.Black,
                fontWeight = FontWeight.Bold)

            Text(text = "Login",
                Modifier.clickable {navController.navigate(Routes.login)},
                color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "or",
            color = Color.Black,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Sign up with",
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