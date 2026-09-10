namespace RoQuiApi.RoQui.Document.Withhold.Dto;

using System.ComponentModel.DataAnnotations;

public class WithholdSupportDto
{
    [Required]
    [RegularExpression(@"^\d{2}$", ErrorMessage = "CodeSupport must contain exactly 2 digits.")]
    public required string CodeSupport { get; set; }

    [Required]
    [RegularExpression(@"^\d{2}$", ErrorMessage = "CodeDocumentSupport must contain exactly 2 digits.")]
    public required string CodeDocumentSupport { get; set; }

    [Required]
    [StringLength(20, ErrorMessage = "NumberDocumentSupport cannot exceed 20 characters.")]
    public required string NumberDocumentSupport { get; set; }

    [Required]
    public required DateTime DateDocumentSupport { get; set; }

    [Required]
    [RegularExpression(@"^\d{10}$|^\d{37}$|^\d{49}$", ErrorMessage = "AuthorizationDocumentSupport must contain 10, 37 or 49 digits.")]
    public required string AuthorizationDocumentSupport { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "TotalWithoutTaxes cannot be negative.")]
    public required decimal TotalWithoutTaxes { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "Total cannot be negative.")]
    public required decimal Total { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The support document must contain at least one withheld tax.")]
    public virtual required ICollection<WithholdDetailDto> WithholdDetails { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The support document must contain at least one document tax.")]
    public virtual required ICollection<WithholdDocumentTaxDto> WithholdDocumentTaxes { get; set; }
}
