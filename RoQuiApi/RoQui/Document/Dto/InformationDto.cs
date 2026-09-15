namespace RoQuiApi.RoQui.Document.Dto;

using System.ComponentModel.DataAnnotations;

public class InformationDto
{
    [Required]
    public required string Name { get; set; }

    [Required]
    public required string Value { get; set; }
}