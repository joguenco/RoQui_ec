namespace RoQuiApi.RoQui.Electronic.Repository;

using RoQuiApi.Data;
using RoQuiApi.RoQui.Electronic.Model;

public class ElectronicRepo : IElectronicRepo
{
    private readonly AppDbContext _context;

    public ElectronicRepo(AppDbContext context)
    {
        _context = context;
    }

    public Parameter? GetParameterByName(string name)
    {
        return _context.Parameters.FirstOrDefault(p => p.Name == name);
    }
}