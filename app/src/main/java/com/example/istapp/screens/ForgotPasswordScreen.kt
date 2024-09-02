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
fun ForgotPasswordScreen(navController: NavController){

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Red,
        contentColor = Color.White
    )

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

        Text(text = "Forgot Password?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "Please enter your Email to reset your password",
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

        Button(onClick = {},
            colors = buttonColors,
            modifier = Modifier.width(150.dp),
        ) {
            Text(text = "Reset Password")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "Don't have an account? ",
                color = Color.Black,
                fontWeight = FontWeight.Bold)

            Text(text = "Sign Up",
                Modifier.clickable {navController.navigate(Routes.signup)},
                color = Color.Gray)
        }
    }
}
