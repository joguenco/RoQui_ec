namespace RoQuiApi.RoQui.Shared;

using System.ComponentModel.DataAnnotations;
using RoQuiApi.RoQui.Head.Repository;

// Se pone [AccessKey] en el DTO y la validacion del framework devuelve 400 sola,
// sin tocar los controladores. El mensaje dice que campo de la clave esta mal.
[AttributeUsage(AttributeTargets.Property)]
public class AccessKeyAttribute : ValidationAttribute
{
    protected override ValidationResult? IsValid(object? value, ValidationContext context)
    {
        var accessKey = value as string;
        // El RUC va primero: si solo se cambio el RUC, el digito verificador
        // tambien falla, pero lo que hay que saber es que el RUC no es el nuestro.
        var error = ValidateTaxpayer(accessKey, context) ?? AccessKey.Validate(accessKey);

        return error is null
            ? ValidationResult.Success
            : new ValidationResult(error, [context.MemberName ?? nameof(AccessKey)]);
    }

    // El RUC de la clave tiene que ser el del contribuyente de la tabla taxpayers,
    // si no el SRI la rechaza igual.
    private static string? ValidateTaxpayer(string? accessKey, ValidationContext context)
    {
        // Sin 49 digitos no hay RUC que leer, eso ya lo dice Validate.
        var ruc = AccessKey.Ruc(accessKey);
        if (ruc is null)
        {
            return null;
        }

        var taxpayerRepo = context.GetRequiredService<ITaxpayerRepo>();

        if (taxpayerRepo.CountTaxpayers() == 0)
        {
            return "There is no taxpayer registered.";
        }

        return taxpayerRepo.GetTaxpayerByIdentification(ruc) is null
            ? $"The RUC {ruc} in the accesskey is not the taxpayer RUC."
            : null;
    }
}
