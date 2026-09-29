namespace RoQuiApi.RoQui.Shared;

using System.ComponentModel.DataAnnotations;

// Se pone [AccessKey] en el DTO y la validacion del framework devuelve 400 sola,
// sin tocar los controladores. El mensaje dice que campo de la clave esta mal.
[AttributeUsage(AttributeTargets.Property)]
public class AccessKeyAttribute : ValidationAttribute
{
    protected override ValidationResult? IsValid(object? value, ValidationContext context)
    {
        var error = AccessKey.Validate(value as string);

        return error is null
            ? ValidationResult.Success
            : new ValidationResult(error, [context.MemberName ?? nameof(AccessKey)]);
    }
}
