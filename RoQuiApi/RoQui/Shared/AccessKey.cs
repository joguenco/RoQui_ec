namespace RoQuiApi.RoQui.Shared;

using System.Globalization;

// Los 9 campos de la clave de acceso, tabla 1 de la ficha tecnica del SRI.
// Cada campo es un trozo de la misma cadena de 49 digitos:
//
//   29092026 01 0402197214001 1 001001 000000901 12345678 1 9
//   fecha    doc  ruc        amb serie secuencial  codigo  e v
public static class AccessKey
{
    private const int Length = 49;

    // Tabla 3: los comprobantes que se pueden emitir.
    private static readonly string[] DocumentCodes = ["01", "03", "04", "05", "06", "07"];

    // Tabla 4: 1 pruebas, 2 produccion.
    private static readonly string[] Environments = ["1", "2"];

    // Tabla 2: por ahora solo existe la emision normal.
    private const string NormalEmission = "1";

    // Devuelve null si la clave esta bien, o el motivo si no.
    public static string? Validate(string? accessKey)
    {
        if (string.IsNullOrEmpty(accessKey) || accessKey.Length != Length)
        {
            return "The access key must contain exactly 49 digits.";
        }

        foreach (var character in accessKey)
        {
            if (!char.IsAsciiDigit(character))
            {
                return "The access key must contain only digits.";
            }
        }

        // 1. Fecha de emision, ddmmaaaa. No basta con que sean 8 digitos: 32132026
        // tambien lo son.
        if (!DateTime.TryParseExact(
                accessKey[..8],
                "ddMMyyyy",
                CultureInfo.InvariantCulture,
                DateTimeStyles.None,
                out _))
        {
            return "The issue date in the access key is not a valid date.";
        }

        // 2. Tipo de comprobante.
        if (!DocumentCodes.Contains(accessKey[8..10]))
        {
            return "The document type in the access key is not valid.";
        }

        // 3. RUC del emisor. El digito verificador no se comprueba aqui: son 13
        // digitos, la provincia y el establecimiento.
        var province = int.Parse(accessKey[10..12]);
        if (province is not (>= 1 and <= 24) and not 30)
        {
            return "The province in the access key RUC is not valid.";
        }

        if (accessKey[20..23] == "000")
        {
            return "The establishment in the access key RUC cannot be 000.";
        }

        // 4. Tipo de ambiente.
        if (!Environments.Contains(accessKey[23..24]))
        {
            return "The environment in the access key must be 1 or 2.";
        }

        // 5. Serie del establecimiento y punto de emision.
        if (accessKey[24..30] == "000000")
        {
            return "The series in the access key cannot be zeros.";
        }

        // 6. Secuencial.
        if (accessKey[30..39] == "000000000")
        {
            return "The sequential in the access key cannot be zeros.";
        }

        // 7. El codigo numerico son 8 digitos y ya quedaron comprobados arriba.

        // 8. Tipo de emision.
        if (accessKey[47..48] != NormalEmission)
        {
            return "The emission type in the access key must be 1.";
        }

        // 9. Digito verificador.
        if (Module11.CheckDigit(accessKey[..48]) != accessKey[48] - '0')
        {
            return "The access key check digit is not valid.";
        }

        return null;
    }
}
