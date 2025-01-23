package com.kiologyn.expenda.presentation.ui.record.modify.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.data.db.entity.Account

@Composable
fun AccountPicker(
    modifier: Modifier = Modifier,
    accountState: MutableState<Account?>,
) {
    val accountText by remember(accountState.value) {
        mutableStateOf(accountState.value?.run { "$name ($currencyCode)" })
    }
    var menuOpened by remember { mutableStateOf(false) }

    var accounts: List<Account> by remember { mutableStateOf(emptyList()) }
    LaunchedEffect(Unit) {
        ExpendaApp.database.apply {
            accounts = accountDao().getAll()
        }
    }

    Box(
        modifier = modifier.clickable { menuOpened = true },
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = accountText ?: stringResource(R.string.record_edit__account_picker_placeholder),
            transitionSpec = {
                val duration = 300 // ms
                fadeIn(tween(duration)) togetherWith fadeOut(tween(duration))
            },
            contentAlignment = Alignment.Center,
            label = "account",
        ) { text ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                textAlign = TextAlign.Center,
                color =
                    if (accountText == null) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                ,
            )
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
            ,
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = Color.Gray,
        )

        DropdownMenu(
            expanded = menuOpened,
            onDismissRequest = { menuOpened = false },
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = account.name,
                            fontSize = 18.sp,
                        )
                    },
                    onClick = {
                        accountState.value = account
                        menuOpened = false
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                    ,
                )
            }
        }
    }
}