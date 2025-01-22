package com.kiologyn.expenda.presentation.ui.accounts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.kiologyn.expenda.app.android.ExpendaApp
import com.kiologyn.expenda.data.db.entity.Account
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaDialog
import com.kiologyn.expenda.presentation.common.sharedcomponent.ExpendaTopBarActivity
import com.kiologyn.expenda.presentation.theme.Black40
import com.kiologyn.expenda.presentation.theme.Black50
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale

class AccountsActivity : ExpendaTopBarActivity() {
    override val title: String = "Edit accounts"
    override val actions: @Composable() (RowScope.() -> Unit) = {
        var dialogOpened by remember { mutableStateOf(false) }
        IconButton(onClick = {
            dialogOpened = true
        }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create account",
            )
        }
        
        EditAccountDialog(
            opened = dialogOpened,
            account = Account(
                name = "",
                currencyCode = Currency.getInstance(Locale.getDefault()).currencyCode,
            ),
            onConfirm = { name, currencyCode ->
                lifecycleScope.launch {
                    ExpendaApp.database.apply {
                        accountDao().insert(Account(
                            name = name,
                            currencyCode = currencyCode,
                        ))
                    }
                }.invokeOnCompletion {
                    dialogOpened = false
                    refreshAccounts = true
                }
            },
            onDelete = null,
            onDismiss = {
                dialogOpened = false
            },
        )
    }
    
    private var refreshAccounts by mutableStateOf(true)

    @Composable
    override fun Content() {
        val accounts = remember { mutableStateListOf<Account>() }
        var modifyingAccountIndex: Int? by remember { mutableStateOf(null) }
        val dismissModifying: () -> Unit = {
            modifyingAccountIndex = null
        }
        LaunchedEffect(refreshAccounts) {
            if (refreshAccounts) {
                ExpendaApp.database.apply {
                    accounts.clear()
                    accounts.addAll(accountDao().getAll())
                }
                refreshAccounts = false
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(accounts) { index, account ->
                AccountCard(
                    name = account.name,
                    currency = Currency.getInstance(account.currencyCode),
                    onClick = {
                        modifyingAccountIndex = index
                    }
                )
            }
        }
        
        if (modifyingAccountIndex != null) {
            val account = accounts[modifyingAccountIndex!!]
            EditAccountDialog(
                opened = true,
                account = account,
                onConfirm = { name, currencyCode ->
                    lifecycleScope.launch {
                        ExpendaApp.database.apply {
                            accountDao().update(Account(
                                id = account.id,
                                name = name,
                                currencyCode = currencyCode,
                            ))
                        }
                    }.invokeOnCompletion {
                        dismissModifying()
                        refreshAccounts = true
                    }
                },
                onDelete = {
                    lifecycleScope.launch {
                        ExpendaApp.database.apply {
                            accountDao().delete(account)
                        }
                    }.invokeOnCompletion {
                        dismissModifying()
                        refreshAccounts = true
                    }
                },
                onDismiss = dismissModifying,
            )
        }
    }

    @Composable
    private fun AccountCard(
        modifier: Modifier = Modifier,
        name: String,
        currency: Currency,
        onClick: () -> Unit,
    ) {
        ListItem(
            modifier = modifier.clickable(onClick = onClick),
            headlineContent = {
                Text(name)
            },
            trailingContent = {
                Text(
                    currency.currencyCode,
                    color = Color.White,
                    fontSize = 16.sp,
                )
            }
        )
    }

    @Composable
    private fun EditAccountDialog(
        opened: Boolean,
        account: Account,
        onConfirm: (String, String) -> Unit,
        onDelete: (() -> Unit)?,
        onDismiss: () -> Unit,
    ) {
        var name by remember { mutableStateOf(account.name) }
        var currency by remember { mutableStateOf(Currency.getInstance(account.currencyCode)) }
        
        ExpendaDialog(
            opened = opened,
            onDismiss = onDismiss,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                    ,
                    text = "Edit account",
                    style = MaterialTheme.typography.labelMedium
                )
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    TextField(
                        value = name,
                        onValueChange = { value -> name = value },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Name", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            
                            unfocusedContainerColor = Black40,
                            focusedContainerColor = Black50,
                        )
                    )
                    
                    var menuOpened by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { menuOpened = true }
                            .height(50.dp)
                            .background(Black40, RoundedCornerShape(15.dp))
                            .clip(RoundedCornerShape(15.dp))
                        ,
                        contentAlignment = Alignment.Center,
                    ) {
                        AnimatedContent(
                            targetState = currency?.currencyCode ?: "Choose an account",
                            transitionSpec = {
                                val duration = 300 // ms
                                fadeIn(tween(duration)) togetherWith fadeOut(tween(duration))
                            },
                            contentAlignment = Alignment.Center,
                            label = "choose account's animation",
                        ) { text ->
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = text,
                                textAlign = TextAlign.Center,
                                color =
                                    if (currency == null) MaterialTheme.colorScheme.onSurfaceVariant
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
                            Currency.getAvailableCurrencies().sortedBy { it.currencyCode }.forEach { currencyEntry ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${currencyEntry.currencyCode} - ${currencyEntry.displayName}",
                                            fontSize = 16.sp,
                                        )
                                    },
                                    onClick = {
                                        currency = currencyEntry
                                        menuOpened = false
                                    },
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                )
                            }
                        }
                    }
                }
                
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                    ,
                ) {
                    if (onDelete != null) {
                        IconButton(onClick = {
                            onDelete()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "delete account",
                                tint = Color.Red,
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text("Cancel")
                    }
                    
                    TextButton(onClick = {
                        onConfirm(name, currency.currencyCode)
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}