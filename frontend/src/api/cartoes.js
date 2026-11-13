const BASE = '/api/cartoes'

async function req(url, options) {
  const resp = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!resp.ok) {
    const textoPuro = resp.headers.get('Content-Type')?.startsWith('text/plain')
    const mensagem = textoPuro ? await resp.text() : ''
    throw new Error(mensagem || `erro ${resp.status}`)
  }
  return resp.status === 204 ? null : resp.json()
}

export const listar = () => req(BASE)
export const criar = (cartao) => req(BASE, { method: 'POST', body: JSON.stringify(cartao) })
export const mover = (id, coluna) =>
  req(`${BASE}/${id}/coluna`, { method: 'PATCH', body: JSON.stringify({ coluna }) })
export const remover = (id) => req(`${BASE}/${id}`, { method: 'DELETE' })
export const adicionarExtra = (id, tipo, valor) =>
  req(`${BASE}/${id}/extras`, { method: 'POST', body: JSON.stringify({ tipo, valor }) })
