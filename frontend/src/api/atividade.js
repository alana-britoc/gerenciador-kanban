async function buscar(url) {
  const resp = await fetch(url)
  if (!resp.ok) throw new Error(`erro ${resp.status}`)
  return resp.json()
}

export const listarHistorico = () => buscar('/api/historico')
export const listarNotificacoes = () => buscar('/api/notificacoes')
