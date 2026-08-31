namespace RoQuiApi.RoQui.Shared;

public class MessageDto
{
    public required string Title { get; set; }
    public int Status { get; set; } = 200;

    public Error? Errors { get; set; }
}

public class Error
{
    public virtual ICollection<string>? Message { get; set; }
}