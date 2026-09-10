using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Electronic.Model;

[Table("ele_parameters")]
public class Parameter
{
    [Column("id", TypeName = "bigint")]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    public long Id { get; set; }

    [Column("name", TypeName = "varchar")]
    public string? Name { get; set; }

    [Column("value", TypeName = "varchar")]
    public string? Value { get; set; }

    [Column("observation", TypeName = "varchar")]
    public string? Observation { get; set; }

    [Column("type", TypeName = "varchar")]
    public string? Type { get; set; }

    [Column("status", TypeName = "boolean")]
    public bool Status { get; set; } = true;
}
