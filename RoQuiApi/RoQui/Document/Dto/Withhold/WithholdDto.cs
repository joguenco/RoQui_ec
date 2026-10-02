namespace RoQuiApi.RoQui.Document.Withhold.Dto;

using System.ComponentModel.DataAnnotations;
using RoQuiApi.RoQui.Shared;

public class WithholdDto : IValidatableObject
{
    [Required]
    public required string Code { get; set; }

    [Required]
    public required string Number { get; set; }

    [Required]
    public required DateTime Date { get; set; }

    [Required]
    [IdentificationType]
    public required string IdentificationType { get; set; }

    [Required]
    public required string Identification { get; set; }

    [Required]
    public required string LegalName { get; set; }

    [Required]
    [AccessKey]
    public required string AccessKey { get; set; }

    [Required]
    [RegularExpression(@"^(0[1-9]|1[0-2])/\d{4}$", ErrorMessage = "FiscalPeriod must have the format MM/YYYY.")]
    public required string FiscalPeriod { get; set; }

    [RegularExpression(@"^(SI|NO)$", ErrorMessage = "Related must be either 'SI' or 'NO'.")]
    public string? Related { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The withhold must contain at least one support document.")]
    public virtual required ICollection<WithholdSupportDto> WithholdSupports { get; set; }

    // La fecha, el codigo y el numero se usan para construir la clave de acceso,
    // asi que tienen que cuadrar con lo que viaja dentro de ella.
    public IEnumerable<ValidationResult> Validate(ValidationContext validationContext)
    {
        foreach (var error in RoQuiApi.RoQui.Shared.AccessKey.ValidateAgainst(AccessKey, Code, Number, Date, "07"))
        {
            yield return new ValidationResult(error);
        }
    }
}
