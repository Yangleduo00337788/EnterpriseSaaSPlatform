import { useEffect, useMemo, useState } from 'react';
import { getDictDataByCode } from '@/api/dict';
import type { DictDataVO } from '@/types';

interface DictOption {
  label: string;
  value: string;
}

export function useDictOptions(dictCode: string, fallback: DictOption[] = []) {
  const [items, setItems] = useState<DictDataVO[]>([]);

  useEffect(() => {
    let mounted = true;
    getDictDataByCode(dictCode)
      .then((res) => {
        if (mounted) {
          setItems((res.data || []).filter((item) => item.status === 1));
        }
      })
      .catch(() => {
        if (mounted) {
          setItems([]);
        }
      });
    return () => {
      mounted = false;
    };
  }, [dictCode]);

  const options = useMemo(() => {
    const fallbackMap = new Map(fallback.map((item) => [item.value, item]));
    items.forEach((item) => {
      fallbackMap.set(item.dictValue, { label: item.dictLabel, value: item.dictValue });
    });
    return Array.from(fallbackMap.values());
  }, [fallback, items]);

  const labelMap = useMemo(
    () => Object.fromEntries(options.map((item) => [item.value, item.label])),
    [options],
  );

  return { options, labelMap, items };
}
