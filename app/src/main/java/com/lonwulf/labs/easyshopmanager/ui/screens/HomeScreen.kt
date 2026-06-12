package com.lonwulf.labs.easyshopmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.domain.uiState.ActivityAction
import com.lonwulf.labs.easyshopmanager.domain.uiState.editedHowLongAgo
import com.lonwulf.labs.easyshopmanager.navigation.Destinations
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.presentation.ui.components.ButtonComponent
import com.lonwulf.labs.easyshopmanager.presentation.ui.components.CardWithTitleComponent
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

class HomeScreenComposable(private val mainViewModel: MainViewModel) : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController
    ) {
        HomeScreen(mainViewModel = mainViewModel, navHostController = navHostController)
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, mainViewModel: MainViewModel, navHostController: NavHostController) {
    val dashboardState by mainViewModel.dashboardState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CardWithTitleComponent(modifier = Modifier.weight(1f), title = "Total Products") {
                Text(
                    text = dashboardState.productsTotal.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(10.dp))

            CardWithTitleComponent(modifier = Modifier.weight(1f), title = "Low Stock") {
                Text(
                    text = dashboardState.lowStockTotal.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.errorContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        CardWithTitleComponent(modifier = Modifier.fillMaxWidth(), title = "Inventory Value") {
            Text(
                text = dashboardState.inventoryValue.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(5.dp))

        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ButtonComponent(
                modifier = Modifier.weight(1f),
                text = "Stock In",
                leadingIcon = {
                    Icon(
                        Icons.Default.Add,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = null
                    )
                },
            ) {
                navHostController.navigate(Destinations.ManualInputScreen.route)
            }
            Spacer(Modifier.width(10.dp))
            ButtonComponent(
                modifier = Modifier.weight(1f),
                text = "Stock Out",
                leadingIcon = {
                    Icon(
                        Icons.Default.Remove,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = null
                    )
                },
            ) {

            }
        }
        //sell or put up stock up for sale to other shops
        ButtonComponent(
            text = "Quick Sale",
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            leadingIcon = {
                Icon(
                    Icons.Default.ShoppingCartCheckout,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null
                )
            },
        ) {

        }
        Spacer(Modifier.height(5.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                tint = MaterialTheme.colorScheme.errorContainer,
                contentDescription = null
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = "Low Stock Alert",
                style = MaterialTheme.typography.titleMedium,
            )
        }

        dashboardState.lowStock.forEach { product ->
            val msg = buildString {
                append(product.name)
                append(" quantity ")
                append("(")
                append(product.quantity)
                append(")")
                append(" is getting low")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.onError.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp, horizontal = 6.dp)
            ) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Spacer(Modifier.height(5.dp))

        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium,
        )

        dashboardState.recentActivity.forEach { activity ->
            CardWithTitleComponent(modifier = Modifier.fillMaxWidth(), title = activity.productName) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = activity.dateTime.editedHowLongAgo())
                    ActivityActionView(action = activity.action, activity.qty)
                }
            }
        }

    }
}

@Composable
fun ActivityActionView(
    action: ActivityAction,
    qty: Int,
    modifier: Modifier = Modifier
) {
    when (action) {
        ActivityAction.QTY_INCREASE,
        ActivityAction.QTY_DECREASE -> QuantityAlertComponent(action = action, modifier = modifier, qty = qty)

        ActivityAction.DESCRIPTION,
        ActivityAction.IMAGE_INPUT,
        ActivityAction.PRICE_CHANGE -> {
            Text(
                text = action.actionName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun QuantityAlertComponent(
    action: ActivityAction,
    qty: Int,
    modifier: Modifier = Modifier
) {
    val isIncrease = action == ActivityAction.QTY_INCREASE
    val qtyIcon = if (isIncrease) Icons.Default.Add else Icons.Default.Remove
    val color = if (isIncrease) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.1.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = qtyIcon,
            tint = color,
            contentDescription = action.actionName
        )
        Text(
            text = qty.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = color
        )
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(navHostController = NavHostController(LocalContext.current), mainViewModel = koinViewModel())

}