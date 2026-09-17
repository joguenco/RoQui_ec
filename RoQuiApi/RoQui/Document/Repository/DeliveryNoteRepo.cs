namespace RoQuiApi.RoQui.Invoice.Repository;

using RoQuiApi.Data;
using RoQuiApi.RoQui.Invoice.Model;

public class DeliveryNoteRepo : IDeliveryNoteRepo
{
    private readonly AppDbContext _context;

    public DeliveryNoteRepo(AppDbContext context)
    {
        _context = context;
    }

    public void CreateDeliveryNote(DeliveryNote deliveryNote)
    {
        ArgumentNullException.ThrowIfNull(deliveryNote);
        _context.DeliveryNotes.Add(deliveryNote);
    }

    public void DeleteDeliveryNote(DeliveryNote deliveryNote)
    {
        ArgumentNullException.ThrowIfNull(deliveryNote);
        _context.DeliveryNotes.Remove(deliveryNote);
    }

    public DeliveryNote? GetDeliveryNoteByCodeAndNumber(string code, string number)
    {
        return _context.DeliveryNotes.FirstOrDefault(g => g.Code == code && g.Number == number);
    }

    public bool SaveChanges()
    {
        return _context.SaveChanges() >= 0;
    }
}
