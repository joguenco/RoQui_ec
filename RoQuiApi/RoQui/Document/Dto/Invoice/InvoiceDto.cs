namespace RoQuiApi.RoQui.Document.Invoice.Dto;

using System.ComponentModel.DataAnnotations;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Document.Dto;

public class InvoiceDto : IValidatableObject
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

    public string? DeliveryNote { get; set; }
    [Required]
    [AccessKey]
    public required string AccessKey { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The invoice must contain at least one detail.")]
    public virtual required ICollection<InvoiceDetailDto> InvoiceDetails { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The invoice must contain at least one payment.")]
    public virtual required ICollection<PaymentDto> Payments { get; set; }

    public virtual ICollection<InformationDto>? Informations { get; set; }

    // La fecha, el codigo y el numero se usan para construir la clave de acceso,
    // asi que tienen que cuadrar con lo que viaja dentro de ella.
    public IEnumerable<ValidationResult> Validate(ValidationContext validationContext)
    {
        foreach (var error in RoQuiApi.RoQui.Shared.AccessKey.ValidateAgainst(AccessKey, Code, Number, Date, "01"))
        {
            yield return new ValidationResult(error);
        }
    }
}
