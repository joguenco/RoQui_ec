namespace RoQuiApi.RoQui.Invoice.Dto;

using System.ComponentModel.DataAnnotations;
using RoQuiApi.RoQui.Shared;

public class LiquidationDto : IValidatableObject
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
    public required string Address { get; set; }

    [Required]
    [AccessKey]
    public required string AccessKey { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The liquidation must contain at least one detail.")]
    public virtual required ICollection<LiquidationDetailDto> LiquidationDetails { get; set; }

    // La fecha, el codigo y el numero se usan para construir la clave de acceso,
    // asi que tienen que cuadrar con lo que viaja dentro de ella.
    public IEnumerable<ValidationResult> Validate(ValidationContext validationContext)
    {
        foreach (var error in RoQuiApi.RoQui.Shared.AccessKey.ValidateAgainst(AccessKey, Code, Number, Date, "03"))
        {
            yield return new ValidationResult(error);
        }
    }
}
