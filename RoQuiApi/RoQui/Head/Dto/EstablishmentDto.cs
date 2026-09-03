namespace RoQuiApi.RoQui.Head.Dto;

using System.ComponentModel.DataAnnotations;

public class EstablishmentDto
{
    [Required]
    [RegularExpression(@"^\d{3}$", ErrorMessage = "Code must contain exactly 3 digits.")]
    public required string Code { get; set; }

    public string? BusinessName { get; set; }

    [Required]
    public required string Address { get; set; }

    [Required]
    public required bool IsPrincipal { get; set; }
}