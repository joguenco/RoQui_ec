namespace RoQuiApi.RoQui.Head.Dto;

using System.ComponentModel.DataAnnotations;

[AttributeUsage(AttributeTargets.Property)]
public class EstablishmentAttribute : ValidationAttribute
{
    protected override ValidationResult? IsValid(object? value, ValidationContext validationContext)
    {
        if (value is not IEnumerable<EstablishmentDto> establishments)
        {
            return ValidationResult.Success;
        }

        if (establishments.GroupBy(establishment => establishment.Code).Any(group => group.Count() > 1))
        {
            return new ValidationResult(
                "Establishment codes must be unique",
                [validationContext.MemberName ?? nameof(TaxpayerDto.Establishments)]);
        }

        if (establishments.Count(establishment => establishment.IsPrincipal) != 1)
        {
            return new ValidationResult(
                "Exactly one establishment must be marked as principal",
                [validationContext.MemberName ?? nameof(TaxpayerDto.Establishments)]);
        }

        return ValidationResult.Success;
    }
}