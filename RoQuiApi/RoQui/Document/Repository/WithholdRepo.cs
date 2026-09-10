namespace RoQuiApi.RoQui.Invoice.Repository;

using RoQuiApi.Data;
using RoQuiApi.RoQui.Invoice.Model;

public class WithholdRepo : IWithholdRepo
{
    private readonly AppDbContext _context;

    public WithholdRepo(AppDbContext context)
    {
        _context = context;
    }

    public void CreateWithhold(Withhold withhold)
    {
        ArgumentNullException.ThrowIfNull(withhold);
        _context.Withholds.Add(withhold);
    }

    public void DeleteWithhold(Withhold withhold)
    {
        ArgumentNullException.ThrowIfNull(withhold);
        _context.Withholds.Remove(withhold);
    }

    public Withhold? GetWithholdByCodeAndNumber(string code, string number)
    {
        return _context.Withholds.FirstOrDefault(w => w.Code == code && w.Number == number);
    }

    public bool SaveChanges()
    {
        return _context.SaveChanges() >= 0;
    }
}
