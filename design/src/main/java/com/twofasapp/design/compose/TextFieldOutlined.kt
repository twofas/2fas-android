import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.twofasapp.design.theme.textFieldDisabledText
import com.twofasapp.resources.R

@Composable
fun TextFieldOutlined(
    value: String,
    onValueChange: (String) -> Unit,
    modifierColumn: Modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    error: String = "",
    isError: Boolean = error.isNotEmpty(),
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    maxChars: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        disabledTextColor = MaterialTheme.colors.textFieldDisabledText
    )
) {
    var textValue by remember { mutableStateOf(TextFieldValue(value, selection = TextRange(value.length))) }

    Column(
        modifier = modifierColumn
    ) {
        OutlinedTextField(
            enabled = enabled,
            readOnly = readOnly,
            value = textValue,
            onValueChange = {
                if (it.text.length <= maxChars) {
                    textValue = it
                    onValueChange.invoke(it.text)
                }
            },
            modifier = modifier.fillMaxWidth(),
            singleLine = singleLine,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon ?: {
                if (error.isNotEmpty())
                    Icon(painterResource(id = R.drawable.ic_error), "error", tint = MaterialTheme.colors.error)
            },
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors,
        )

        AnimatedVisibility(visible = isError) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }

        AnimatedVisibility(visible = isError.not()) {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}