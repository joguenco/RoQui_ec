namespace RoQuiApi.RoQui.Document.DeliveryNote.Dto;

using System.ComponentModel.DataAnnotations;
using RoQuiApi.RoQui.Shared;

public class DeliveryNoteDto : IValidatableObject
{
    [Required]
    public required string Code { get; set; }

    [Required]
    public required string Number { get; set; }

    [Required]
    public required DateTime Date { get; set; }

    [Required]
    [StringLength(300, ErrorMessage = "AddressStart cannot exceed 300 characters.")]
    public required string AddressStart { get; set; }

    [Required]
    [IdentificationType]
    public required string CarrierIdentificationType { get; set; }

    [Required]
    public required string CarrierIdentification { get; set; }

    [Required]
    public required string CarrierLegalName { get; set; }

    [Required]
    [StringLength(20, ErrorMessage = "Plate cannot exceed 20 characters.")]
    public required string Plate { get; set; }

    [Required]
    public required DateTime DateStartTransport { get; set; }

    [Required]
    public required DateTime DateEndTransport { get; set; }

    [StringLength(300, ErrorMessage = "Observation cannot exceed 300 characters.")]
    public string? Observation { get; set; }

    [Required]
    [AccessKey]
    public required string AccessKey { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The delivery note must contain at least one receiver.")]
    public virtual required ICollection<DeliveryNoteReceiverDto> DeliveryNoteReceivers { get; set; }

    // La fecha, el codigo y el numero se usan para construir la clave de acceso,
    // asi que tienen que cuadrar con lo que viaja dentro de ella.
    public IEnumerable<ValidationResult> Validate(ValidationContext validationContext)
    {
        foreach (var error in RoQuiApi.RoQui.Shared.AccessKey.ValidateAgainst(AccessKey, Code, Number, Date, "06"))
        {
            yield return new ValidationResult(error);
        }
    }
}
