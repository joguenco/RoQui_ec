const headerAuthorization = (token) => {
  return {
    headers: { Authorization: `Bearer ${token}` },
  }
}

export default headerAuthorization
