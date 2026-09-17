namespace RoQuiApi.RoQui.Document.DeliveryNote.Dto;

using System.ComponentModel.DataAnnotations;

public class DeliveryNoteReceiverDetailDto
{
    [Required]
    [Range(1, int.MaxValue, ErrorMessage = "Line must be greater than zero.")]
    public required int Line { get; set; }

    [Required]
    [StringLength(25, ErrorMessage = "PrincipalCode cannot exceed 25 characters.")]
    public required string PrincipalCode { get; set; }

    [Required]
    [StringLength(300, ErrorMessage = "Name cannot exceed 300 characters.")]
    public required string Name { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "Quantity cannot be negative.")]
    public required decimal Quantity { get; set; }
}
