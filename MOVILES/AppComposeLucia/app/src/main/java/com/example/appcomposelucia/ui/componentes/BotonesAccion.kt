package com.example.appcomposelucia.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcomposelucia.common.Constantes
import com.example.appcomposelucia.ui.theme.BtnActualizarColor
import com.example.appcomposelucia.ui.theme.BtnBorrarColor
import com.example.appcomposelucia.ui.theme.BtnGuardarColor
import com.example.appcomposelucia.ui.theme.BtnLimpiarColor
import com.example.appcomposelucia.ui.theme.Dimens



@Composable
fun BotonesActtion(
    enableBorrar : Boolean,
    enableActualizar : Boolean,
    onGuardar: () -> Unit,
    onLimpiarFormulario: () -> Unit,
    onBorrar: () -> Unit,
    onActualizar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Button(
            onClick = { onLimpiarFormulario() },
            colors = ButtonDefaults.buttonColors(
                containerColor = BtnLimpiarColor
            ),
            modifier = Modifier
                .weight(0.85f)
                .height(Dimens.buttonHeightMedium)
        ) {
            Text(
                text = Constantes.LIMPIAR,
                fontSize = 11.sp
            )
        }

        Button(
            onClick = { onActualizar() },
            enabled = enableActualizar,
            colors = ButtonDefaults.buttonColors(
                containerColor = BtnActualizarColor,
                disabledContainerColor = BtnActualizarColor.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .weight(1f)
                .height(Dimens.buttonHeightMedium)
        ) {
            Text(
                text = Constantes.ACTUALIZAR,
                fontSize = 11.sp
            )
        }

        Button(
            onClick = { onBorrar() },
            enabled = enableBorrar,
            colors = ButtonDefaults.buttonColors(
                containerColor = BtnBorrarColor,
                disabledContainerColor = BtnBorrarColor.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .weight(0.8f)
                .height(Dimens.buttonHeightMedium)
        ) {
            Text(
                text = Constantes.BORRAR,
                fontSize = 11.sp
            )
        }

        Button(
            onClick = { onGuardar() },
            colors = ButtonDefaults.buttonColors(
                containerColor = BtnGuardarColor
            ),
            modifier = Modifier
                .weight(0.9f)
                .height(Dimens.buttonHeightMedium)
        ) {
            Text(
                text = Constantes.GUARDAR,
                fontSize = 11.sp
            )
        }
    }


}
