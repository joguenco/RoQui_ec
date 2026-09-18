namespace RoQuiApi.RoQui.Document.Dto;

using System.ComponentModel.DataAnnotations;

public class DocumentStatusDto
{
    [Required]
    public required string Code { get; set; }
    [Required]
    public required string Number { get; set; }
}