package com.lonwulf.labs.easyshopmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.domain.model.InputType
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.ui.components.CardWithTitleComponent
import com.lonwulf.labs.easyshopmanager.ui.components.TextInputComponent
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.ProductViewModel
import org.koin.androidx.compose.koinViewModel

class ManualInputScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        ManualInputScreen()
    }
}

@Composable
fun ManualInputScreen(modifier: Modifier = Modifier, productViewModel: ProductViewModel = koinViewModel()) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .verticalScroll(rememberScrollState())
    ) {
        var productName by rememberSaveable { mutableStateOf("") }
        var selectedBrand by rememberSaveable { mutableStateOf("") }
        var productQty by rememberSaveable { mutableStateOf(0) }
        var selectedCategory by rememberSaveable { mutableStateOf("") }
        var sellingPrice by rememberSaveable { mutableStateOf(0.0) }
        var buyingPrice by rememberSaveable { mutableStateOf(0.0) }
        var description by rememberSaveable { mutableStateOf("") }
        var vendor by rememberSaveable { mutableStateOf("") }

        CardWithTitleComponent(title = "Basic information") {
            TextInputComponent(
                value = productName,
                inputType = InputType.Text(keyboardType = KeyboardType.Text),
                onValueChange = { productName = it },
                label = "Product name",
            )
            TextInputComponent(
                value = selectedCategory,
                onValueChange = { selectedCategory = it },
                label = "Category",
                inputType = InputType.Dropdown(
                    options = listOf("Kenya", "Uganda", "Tanzania", "Ethiopia", "Rwanda"),
                    onOptionSelected = { selectedCategory = it },
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                TextInputComponent(
                    modifier = Modifier.weight(1f),
                    value = selectedBrand,
                    onValueChange = { selectedBrand = it },
                    label = "Brand",
                    inputType = InputType.Dropdown(
                        options = listOf("Kenya", "Uganda", "Tanzania", "Ethiopia", "Rwanda"),
                        onOptionSelected = { selectedBrand = it },
                    ),
                )

                TextInputComponent(
                    modifier = Modifier.weight(1f),
                    value = productQty.toString(),
                    inputType = InputType.Text(keyboardType = KeyboardType.Number),
                    onValueChange = { productQty = it.toIntOrNull() ?: 0 },
                    label = "Qty",
                )
            }
        }

        CardWithTitleComponent(title = "Purchase information") {
            TextInputComponent(
                value = buyingPrice.toString(),
                onValueChange = { buyingPrice = it.toDouble() },
                label = "Price (KES)",
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                inputType = InputType.Text(keyboardType = KeyboardType.Decimal),
            )

            TextInputComponent(
                value = vendor,
                inputType = InputType.Text(keyboardType = KeyboardType.Text),
                onValueChange = { vendor = it },
                label = "Vendor",
            )
        }

        CardWithTitleComponent(title = "Selling information") {
            TextInputComponent(
                value = sellingPrice.toString(),
                onValueChange = { sellingPrice = it.toDouble() },
                label = "Price (KES)",
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                inputType = InputType.Text(keyboardType = KeyboardType.Decimal),
            )
            TextInputComponent(
                value = description,
                inputType = InputType.Text(keyboardType = KeyboardType.Text),
                onValueChange = { description = it },
                label = "Description",
            )
        }

        Spacer(Modifier.height(5.dp))

        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = {

            }
        ) {
            Text(
                text = "Save Product",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManualInputScreenPreview() =
    ManualInputScreen(
        modifier = Modifier.padding(10.dp)
    )