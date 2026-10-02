namespace RoQuiApi.RoQui.Shared;

using System.ComponentModel.DataAnnotations;
using System.Text.Json;

// Tabla 6 del SRI. Solo se mira el tipo, el numero lo validan roteg y DonPos.
[AttributeUsage(AttributeTargets.Property)]
public class IdentificationTypeAttribute : ValidationAttribute
{
    // 04 RUC, 05 cedula, 06 pasaporte, 07 consumidor final, 08 del exterior.
    private static readonly string[] Types = ["04", "05", "06", "07", "08"];

    protected override ValidationResult? IsValid(object? value, ValidationContext context)
    {
        // Si no viene o viene vacio, ya lo dice [Required].
        if (value is not string { Length: > 0 } type || Types.Contains(type))
        {
            return ValidationResult.Success;
        }

        var field = JsonNamingPolicy.CamelCase.ConvertName(context.MemberName ?? "identificationType");
        return new ValidationResult(
            $"The {field} {type} is not valid, it must be 04, 05, 06, 07 or 08.",
            [context.MemberName ?? field]);
    }
}
