import { useState } from 'react'

export default function NovoCartao({ onCriar }) {
  const [titulo, setTitulo] = useState('')
  const [tipo, setTipo] = useState('TAREFA')

  function enviar(e) {
    e.preventDefault()
    if (!titulo.trim()) return
    onCriar({ tipo, titulo, descricao: '' })
    setTitulo('')
  }

  return (
    <form className="novo" onSubmit={enviar}>
      <input value={titulo} onChange={(e) => setTitulo(e.target.value)} placeholder="Novo cartão" />
      <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
        <option value="TAREFA">Tarefa</option>
        <option value="BUG">Bug</option>
        <option value="MELHORIA">Melhoria</option>
      </select>
      <button type="submit">Adicionar</button>
    </form>
  )
}
