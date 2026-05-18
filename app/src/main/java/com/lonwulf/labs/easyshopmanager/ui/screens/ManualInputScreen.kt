package com.lonwulf.labs.easyshopmanager.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.R
import com.lonwulf.labs.easyshopmanager.domain.model.InputType
import com.lonwulf.labs.easyshopmanager.domain.uiState.CreateProductState
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.ui.components.AlertDialogType
import com.lonwulf.labs.easyshopmanager.ui.components.AppLoaderComponent
import com.lonwulf.labs.easyshopmanager.ui.components.ButtonComponent
import com.lonwulf.labs.easyshopmanager.ui.components.CardWithTitleComponent
import com.lonwulf.labs.easyshopmanager.ui.components.CustomAlertDialogComponent
import com.lonwulf.labs.easyshopmanager.ui.components.TextInputComponent
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class ManualInputScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        ManualInputScreen(snackbarHostState = snackbarHostState)
    }
}

@Composable
fun ManualInputScreen(
    modifier: Modifier = Modifier,
    productViewModel: ProductViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    var productName by rememberSaveable { mutableStateOf("") }
    var selectedBrand by rememberSaveable { mutableStateOf("") }
    var productQty by rememberSaveable { mutableIntStateOf(0) }
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var selectedSubCategory by rememberSaveable { mutableStateOf("") }
    var sellingPrice by rememberSaveable { mutableDoubleStateOf(0.0) }
    var buyingPrice by rememberSaveable { mutableDoubleStateOf(0.0) }
    var description by rememberSaveable { mutableStateOf("") }
    var vendor by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val createProductState by productViewModel.createProductState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    val brandsState by productViewModel.brandsState.collectAsStateWithLifecycle()
    val brands = remember(brandsState.brands) {
        brandsState.brands.map { it.name }
    }
    val categoriesState by productViewModel.categoriesState.collectAsStateWithLifecycle()
    val categories = remember(categoriesState) {
        categoriesState.categories.map { it.name }
    }
    val subCategoriesState by productViewModel.subCategoriesState.collectAsStateWithLifecycle()
    val subCategories = remember(subCategoriesState) {
        subCategoriesState.subCategories.map { it.name }
    }

    LaunchedEffect(Unit) {
        productViewModel.fetchCachedBrands()
        productViewModel.fetchCachedCategories()
    }

    fun resetFields() {
        productName = ""
        selectedBrand = ""
        productQty = 0
        selectedCategory = ""
        selectedSubCategory = ""
        sellingPrice = 0.0
        buyingPrice = 0.0
        description = ""
        vendor = ""
    }

    when (createProductState) {
        is CreateProductState.Loading -> AppLoaderComponent()
        else -> Unit
    }

    if (showSuccessDialog) {
        CustomAlertDialogComponent(
            type = AlertDialogType.SUCCESS_DIALOG,
            msg = "Operation completed!",
            onDismissRequest = { showSuccessDialog = false },
            onConfirmation = { showSuccessDialog = false },
            titleContentColor = MaterialTheme.colorScheme.primaryContainer,
            textContentColor = Color.Black,
            imageVector = Icons.Filled.CheckCircle,
            iconTint = MaterialTheme.colorScheme.primaryContainer,
            title = stringResource(R.string.success)
        )
    }

    LaunchedEffect(createProductState) {
        when (val state = createProductState) {
            is CreateProductState.Success -> {
                Log.e("ManualInputScreen", "result: ${state.data}")
                resetFields()
                showSuccessDialog = true
            }

            is CreateProductState.Error ->
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Long
                )

            else -> Unit
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CardWithTitleComponent(title = "Basic information") {
            TextInputComponent(
                value = productName,
                inputType = InputType.Text(keyboardType = KeyboardType.Text),
                onValueChange = { productName = it },
                label = "Product name",
                isError = productName.isEmpty(),
                fieldRequired = true,
            )

            TextInputComponent(
                value = selectedCategory,
                onValueChange = { selectedCategory = it },
                label = "Category",
                inputType = InputType.Dropdown(
                    options = categories,
                    onOptionSelected = { selectedCat ->
                        selectedCategory = selectedCat
                        selectedSubCategory = ""
                        val catId = categoriesState.categories.find { it.name == selectedCat }?.id
                        catId?.let {
                            productViewModel.fetchSubCategoriesByCategoryId(it)
                        }
                    },
                ),
                isError = selectedCategory.isEmpty(),
                fieldRequired = true,
            )

            if (selectedCategory.isNotEmpty()) {
                AnimatedVisibility(
                    visible = selectedCategory.isNotEmpty(),
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    TextInputComponent(
                        value = selectedSubCategory,
                        onValueChange = { selectedSubCategory = it },
                        label = "Sub category",
                        inputType = InputType.Dropdown(
                            options = subCategories,
                            onOptionSelected = { selectedSubCategory = it },
                        ),
                        isError = selectedSubCategory.isEmpty(),
                        fieldRequired = true,
                    )
                }
            }

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
                        options = brands,
                        onOptionSelected = { selectedBrand = it },
                    ),
                    isError = selectedBrand.isEmpty(),
                    fieldRequired = true,
                )

                TextInputComponent(
                    modifier = Modifier.weight(1f),
                    value = productQty.toString(),
                    inputType = InputType.Text(keyboardType = KeyboardType.Number),
                    onValueChange = { productQty = it.toIntOrNull() ?: 0 },
                    label = "Qty",
                    isError = productQty == 0,
                    fieldRequired = true,
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
                isError = buyingPrice == 0.0,
                fieldRequired = true,
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
                maxLines = Int.MAX_VALUE,
                isSingleLine = false,
                modifier = Modifier
                    .height(140.dp)
                    .verticalScroll(rememberScrollState()),
                label = "Description",
            )
        }

        Spacer(Modifier.height(10.dp))

        ButtonComponent(text = "Save Product") {
            val (isValid, errorMessage) = productViewModel.validateCreateProduct(
                name = productName,
                brandName = selectedBrand,
                qty = productQty.toString(),
                buyingPrice = buyingPrice,
                vendor = vendor,
                sellingPrice = sellingPrice,
                description = description,
                subCategoryId = 0
            )
            if (isValid) {
                productViewModel.insertProductToCache(
                    name = productName,
                    brandName = selectedBrand,
                    qty = productQty.toString(),
                    buyingPrice = buyingPrice,
                    vendor = vendor,
                    sellingPrice = sellingPrice,
                    description = description,
                    subCategoryId = 0,
                    categoryId = 0
                )
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        errorMessage ?: "Unknown error",
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ManualInputScreenPreview() =
    ManualInputScreen(
        modifier = Modifier.padding(10.dp), snackbarHostState = SnackbarHostState()
    )