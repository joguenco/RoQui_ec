namespace RoQuiApi.RoQui.Invoice.Dto;

using System.ComponentModel.DataAnnotations;

public class DebitNoteDetailDto
{
    [Required]
    [StringLength(300, ErrorMessage = "Reason cannot exceed 300 characters.")]
    public required string Reason { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "Value cannot be negative.")]
    public required decimal Value { get; set; }
}
